package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.games.exceptions.GameRegisteringException;
import fr.efreicraft.ludos.core.games.exceptions.GameStatusException;
import fr.efreicraft.ludos.core.games.runnables.LobbyCountdown;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Gestionnaire des jeux.<br /><br/>
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GameManager implements IManager {

    /**
     * Status actuel du jeu.
     */
    public enum GameStatus {
        /**
         * État en attente. Les joueurs sont dans le lobby.
         */
        WAITING,

        /**
         * État de démarrage du jeu. Les joueurs sont dans la carte mais sont freeze. La logique du jeu n'est pas encore lancée.
         */
        STARTING,

        /**
         * État de jeu. Les joueurs peuvent jouer et la logique du jeu est lancée.
         */
        INGAME,

        /**
         * État de fin de jeu. Les joueurs sont dans la carte mais la logique du jeu est arrêtée.
         */
        ENDING
    }

    private ArrayList<Plugin> gamePlugins;

    private Game currentGame;
    private Plugin currentPlugin;

    private GameStatus status;

    private LobbyCountdown lobbyCountdown;

    private String defaultGamePluginName;

    /**
     * Constructeur du gestionnaire de jeux. Il vérifie que la classe n'est pas déjà initialisée.
     */
    public GameManager() {
        if(Core.get().getGameManager() != null) {
            throw new IllegalStateException("GameManager already initialized !");
        }
    }

    @Override
    public void runManager() {
        setStatus(GameStatus.WAITING);
        GameServerRedisDispatcher.serverReady();
        loadAllGameJars();
    }

    /**
     * Charge les plugins de jeu disponibles dans le dossier "games" du datafolder.
     */
    public void loadAllGameJars() {
        gamePlugins = new ArrayList<>();
        File gamesFolder = new File(Core.get().getPlugin().getDataFolder(), "games");
        if(!gamesFolder.exists()) {
            gamesFolder.mkdirs();
        }
        File[] files = gamesFolder.listFiles();
        if(files == null) {
            return;
        }
        for(File file : files) {
            if(file.getName().endsWith(".jar")) {
                try {
                    Plugin pl = Core.get().getServer().getPluginManager().loadPlugin(file);
                    Core.get().getServer().getPluginManager().disablePlugin(pl);
                    gamePlugins.add(pl);
                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Charge le jeu demandé.
     * @param gameName Nom du plugin du jeu.
     * @throws GameStatusException Exception levée si le jeu ne peut pas être chargé
     */
    public void loadGame(String gameName) throws GameStatusException {
        if(status != GameStatus.WAITING) {
            throw new GameStatusException("Impossible de charger un jeu en cours de partie !");
        }
        for (Plugin gamePlugin : gamePlugins) {
            if (gamePlugin.getName().equalsIgnoreCase(gameName)) {
                currentPlugin = gamePlugin;
                Core.get().getServer().getPluginManager().enablePlugin(gamePlugin);
            }
        }
    }

    /**
     * Permet de modifier le jeu par défaut. Dans ce cas, à la fin d'une partie, ce jeu sera chargé.<br />
     * Utilisé dans les communications Redis avec le proxy.
     * @param defaultGamePluginName Nom du plugin du jeu.
     */
    public void changeDefaultGame(String defaultGamePluginName) {
        this.defaultGamePluginName = defaultGamePluginName;
        Bukkit.getScheduler().runTask(Core.get().getPlugin(), () -> {
            try {
                loadGame(defaultGamePluginName);
            } catch (GameStatusException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Méthode appelée par le plugin du jeu pour s'enregistrer.
     * @param gameClass Classe du jeu
     */
    public void registerGame(Class<? extends Game> gameClass) throws GameRegisteringException {
        try {
            currentGame = gameClass.getConstructor().newInstance();
            currentGame.prepareServer();
            if(lobbyCountdown != null) {
                lobbyCountdown.cancel();
            }
            lobbyCountdown = new LobbyCountdown(currentGame.getMetadata().rules().startTimer());
            Core.get().getLogger().log(Level.INFO, "Game {0} registered !", gameClass.getPackageName());
            GameServerRedisDispatcher.game();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GameRegisteringException(e.getMessage());
        }
    }

    /**
     * Désenregistre le jeu chargé.
     */
    public void unregisterCurrentGame() {
        if(currentGame == null || currentPlugin == null) {
            return;
        }
        if(lobbyCountdown != null) {
            lobbyCountdown.cancel();
        }
        Core.get().getServer().getPluginManager().disablePlugin(currentPlugin);
        currentGame = null;
        currentPlugin = null;
    }

    /**
     * Réinitialise le serveur de jeu. Décharge la carte, les équipes, change le status
     */
    public void resetServer() {
        Core.get().getMapManager().unloadMap();
        Core.get().getTeamManager().unloadTeams();
        this.defaultGamePluginName = null;
        Core.get().getGameManager().setStatus(GameManager.GameStatus.WAITING);
    }

    /**
     * Génère une liste human-readable des jeux disponibles
     * @return Liste des jeux disponibles
     */
    public List<String> getAvailableGames() {
        ArrayList<String> gamesAvailable = new ArrayList<>();
        for (Plugin gamePlugin : gamePlugins) {
            gamesAvailable.add(gamePlugin.getName());
        }
        return gamesAvailable;
    }

    /**
     * Récupère le jeu actuellement chargé
     * @return Jeu actuellement chargé
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Récupère le plugin du jeu actuellement chargé
     * @return Plugin du jeu actuellement chargé
     */
    public Plugin getCurrentPlugin() {
        return currentPlugin;
    }

    /**
     * Récupère le status actuel du jeu
     * @return Status actuel du jeu
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Démarre le jeu actuellement chargé
     * @throws GameStatusException Exception levée si le jeu ne peut pas être démarré
     */
    public void startCurrentGame() throws GameStatusException {
        if (currentGame == null) {
            throw new GameStatusException("Aucun jeu n'est chargé !");
        }
        if (status != GameStatus.WAITING) {
            throw new GameStatusException("Impossible de démarrer un jeu en cours de partie !");
        }
        if(Core.get().getMapManager().getCurrentMap() == null) {
            throw new GameStatusException("Impossible de démarrer un jeu sans map !");
        }
        if(!Core.get().getMapManager().getCurrentMap().isParsed()) {
            throw new GameStatusException("Impossible de démarrer un jeu sans que la map soit parsée !");
        }

        setStatus(GameStatus.STARTING);
    }

    /**
     * Arrête le jeu actuellement chargé
     * @throws GameStatusException Exception levée si le jeu ne peut pas être arrêté
     */
    public void endCurrentGame() throws GameStatusException {
        if (currentGame == null) {
            throw new GameStatusException("Aucun jeu n'est chargé !");
        }
        if (status == GameStatus.ENDING || status == GameStatus.STARTING || status == GameStatus.WAITING) {
            throw new GameStatusException("Impossible de terminer un jeu qui n'est pas en cours !");
        }
        if (status != GameStatus.INGAME) {
            throw new GameStatusException("Impossible de terminer un jeu qui n'est pas en cours !");
        }

        setStatus(GameStatus.ENDING);
    }

    /**
     * Change le status actuel du jeu
     * @param status Nouveau status du jeu
     */
    public void setStatus(GameStatus status) {
        this.status = status;
        GameServerRedisDispatcher.serverStatus();

        for(Player player : Core.get().getPlayerManager().getPlayers()) {
            player.setupScoreboard();
        }

        if(status == GameStatus.STARTING) {
            currentGame.startGame();
        } else if(status == GameStatus.INGAME) {
            if(!currentGame.checkIfGameHasToBeEnded()) {
                currentGame.beginGame();
            }
        } else if(status == GameStatus.ENDING) {
            currentGame.endGame();
        } else if(status == GameStatus.WAITING) {
            this.unregisterCurrentGame();
            if(defaultGamePluginName != null) {
                try {
                    this.loadGame(defaultGamePluginName);
                } catch (GameStatusException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
