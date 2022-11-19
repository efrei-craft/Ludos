package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.games.interfaces.Game;
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
         * Etat en attente. Les joueurs sont dans le lobby.
         */
        WAITING,

        /**
         * Etat de démarrage du jeu. Les joueurs sont dans la carte mais sont freeze. La logique du jeu n'est pas encore lancée.
         */
        STARTING,

        /**
         * Etat de jeu. Les joueurs peuvent jouer et la logique du jeu est lancée.
         */
        INGAME,

        /**
         * Etat de fin de jeu. Les joueurs sont dans la carte mais la logique du jeu est arrêtée.
         */
        ENDING
    }

    private final ArrayList<Class<? extends Game>> games = new ArrayList<>();

    private Game currentGame;
    private GameStatus status;

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
        loadGameJars();
    }

    /**
     * Charge les jeux disponibles dans le dossier "games" du datafolder.
     */
    private void loadGameJars() {
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
                    if(pl != null) {
                        Core.get().getServer().getPluginManager().enablePlugin(pl);
                    }
                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Charge le jeu demandé.
     * @param gameName Chemin canonique de la classe Main du jeu
     * @throws GameStatusException Exception levée si le jeu ne peut pas être chargé
     */
    public void loadGame(String gameName) throws GameStatusException {
        if(status != GameStatus.WAITING) {
            throw new GameStatusException("Impossible de charger un jeu en cours de partie !");
        }
        for (Class<? extends Game> gameClass : games) {
            if (gameClass.getPackageName().equalsIgnoreCase(gameName)) {
                try {
                    currentGame = gameClass.getConstructor().newInstance();
                    currentGame.prepareServer();
                    Core.get().getLogger().log(Level.INFO, "Game {0} loaded !", gameName);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Enregistre un jeu dans le gestionnaire de jeux.
     * @param gameClass Classe du jeu
     */
    public void registerGame(Class<? extends Game> gameClass) {
        Core.get().getLogger().log(Level.INFO, "Game {0} registered !", gameClass.getPackageName());
        games.add(gameClass);
    }

    /**
     * Génère une liste human-readable des jeux disponibles
     * @return Liste des jeux disponibles
     */
    public List<String> getAvailableGames() {
        ArrayList<String> gamesAvailable = new ArrayList<>();
        for (Class<? extends Game> game : games) {
            gamesAvailable.add(game.getPackageName());
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
            currentGame = null;
        }
    }

}
