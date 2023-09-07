package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.games.exceptions.GameRegisteringException;
import fr.efreicraft.ludos.core.games.exceptions.GameStatusException;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.interfaces.GamePlugin;
import fr.efreicraft.ludos.core.games.runnables.LobbyCountdown;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Gestionnaire des jeux.<br /><br/>
 * <p>
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

    private Map<String, GamePlugin> gamePlugins;

    private Game currentGame;
    private Plugin currentPlugin;

    private GameStatus status;

    private LobbyCountdown lobbyCountdown;

    private String defaultGamePluginName;

    private boolean autoGameStart = true;

    /**
     * Constructeur du gestionnaire de jeux. Il vérifie que la classe n'est pas déjà initialisée.
     */
    public GameManager() {
        if (Core.get().getGameManager() != null) {
            throw new IllegalStateException("GameManager already initialized !");
        }
    }

    @Override
    public void runManager() {
        setStatus(GameStatus.WAITING);
        loadAllGameJars();
    }

    public void unloadAllGameJars() {
        for (GamePlugin gamePlugin : gamePlugins.values()) {
            unloadGameJar(gamePlugin);
        }
        gamePlugins.clear();
        System.gc();
    }

    private void unloadGameJar(GamePlugin plugin) {
        Core.get().getServer().getPluginManager().disablePlugin(plugin);

        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Core.get().getLogger().log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                Core.get().getLogger().log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Charge les plugins de jeu disponibles dans le dossier "games" du datafolder.
     */
    public void loadAllGameJars() {
        gamePlugins = new HashMap<>();
        File gamesFolder = new File(Core.get().getPlugin().getDataFolder(), "games");
        if (!gamesFolder.exists()) {
            gamesFolder.mkdirs();
        }
        File[] files = gamesFolder.listFiles();
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.getName().endsWith(".jar")) {
                try {
                    Plugin pl = Core.get().getServer().getPluginManager().loadPlugin(file);
                    Core.get().getServer().getPluginManager().enablePlugin(pl);
                } catch (InvalidPluginException | InvalidDescriptionException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Charge le jeu demandé.
     *
     * @param gameName Nom du plugin du jeu.
     * @throws GameStatusException Exception levée si le jeu ne peut pas être chargé
     */
    public void loadGame(String gameName) throws GameStatusException, GameRegisteringException {
        if (status != GameStatus.WAITING) {
            throw new GameStatusException("Impossible de charger un jeu en cours de partie !");
        }
        GamePlugin gamePlugin = gamePlugins.get(gameName);

        if (gamePlugin == null) {
            throw new GameRegisteringException("Le jeu " + gameName + " n'est pas enregistré !");
        }

        currentPlugin = gamePlugin;

        try {
            currentGame = gamePlugin.getGameClass().getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new GameRegisteringException("Impossible d'instancier le jeu " + gameName + " !");
        }

        currentGame.prepareServer();
        if (lobbyCountdown != null) {
            lobbyCountdown.cancel();
        }

        lobbyCountdown = new LobbyCountdown(currentGame.getMetadata().rules().startTimer());
        Core.get().getLogger().log(Level.INFO, "Game {0} loaded !", gamePlugin.getGameClass().getPackageName());
        GameServerDispatcher.updateStatus();
    }

    /**
     * Permet de modifier le jeu par défaut. Dans ce cas, à la fin d'une partie, ce jeu sera chargé.<br />
     * Utilisé dans les communications Redis avec le proxy.
     *
     * @param defaultGamePluginName Nom du plugin du jeu.
     */
    public void changeDefaultGame(String defaultGamePluginName) {
        this.defaultGamePluginName = defaultGamePluginName;
        Bukkit.getScheduler().runTask(Core.get().getPlugin(), () -> {
            try {
                loadGame(defaultGamePluginName);
            } catch (GameStatusException | GameRegisteringException e) {
                e.printStackTrace();
            }
        });
    }

    public void registerGamePlugin(GamePlugin gamePlugin) {
        System.out.println("Registering game " + gamePlugin.getName());
        gamePlugins.put(gamePlugin.getName(), gamePlugin);
    }

    /**
     * Désenregistre le jeu chargé.
     */
    public void unregisterCurrentGame() {
        if (currentGame == null || currentPlugin == null) {
            return;
        }
        if (lobbyCountdown != null) {
            lobbyCountdown.cancel();
        }
        currentGame = null;
        currentPlugin = null;

        System.gc();
    }

    /**
     * Réinitialise le serveur de jeu. Décharge la carte, les équipes, change le status
     */
    public void resetServer() {
        Bukkit.getScheduler().runTask(Core.get().getPlugin(), () -> {
            Core.get().getMapManager().unloadMap();
            Core.get().getTeamManager().unloadTeams();
            this.defaultGamePluginName = null;
            Core.get().getGameManager().setStatus(GameManager.GameStatus.WAITING);
        });
    }

    /**
     * Génère une liste human-readable des jeux disponibles
     *
     * @return Liste des jeux disponibles
     */
    public List<String> getAvailableGames() {
        return new ArrayList<>(gamePlugins.keySet());
    }

    /**
     * Récupère le jeu actuellement chargé
     *
     * @return Jeu actuellement chargé
     */
    public Game getCurrentGame() {
        return currentGame;
    }

    /**
     * Récupère le plugin du jeu actuellement chargé
     *
     * @return Plugin du jeu actuellement chargé
     */
    public Plugin getCurrentPlugin() {
        return currentPlugin;
    }

    /**
     * Récupère le nom du plugin du jeu par défaut
     *
     * @return Nom du plugin du jeu par défaut
     */
    public String getDefaultGamePluginName() {
        return defaultGamePluginName;
    }

    /**
     * Récupère le status actuel du jeu
     *
     * @return Status actuel du jeu
     */
    public GameStatus getStatus() {
        return status;
    }

    /**
     * Auto Start des jeux
     *
     * @return Auto Start
     */
    public boolean isAutoGameStart() {
        return autoGameStart;
    }

    /**
     * Auto Start des jeux
     *
     * @param autoGameStart Auto Start
     */
    public void setAutoGameStart(boolean autoGameStart) {
        this.autoGameStart = autoGameStart;
    }

    /**
     * Démarre le jeu actuellement chargé
     *
     * @throws GameStatusException Exception levée si le jeu ne peut pas être démarré
     */
    public void startCurrentGame() throws GameStatusException {
        if (currentGame == null) {
            throw new GameStatusException("Aucun jeu n'est chargé !");
        }
        if (status != GameStatus.WAITING) {
            throw new GameStatusException("Impossible de démarrer un jeu en cours de partie !");
        }
        if (Core.get().getMapManager().getCurrentMap() == null) {
            throw new GameStatusException("Impossible de démarrer un jeu sans map !");
        }
        if (!Core.get().getMapManager().getCurrentMap().isParsed()) {
            throw new GameStatusException("Impossible de démarrer un jeu sans que la map soit parsée !");
        }

        setStatus(GameStatus.STARTING);
    }

    /**
     * Arrête le jeu actuellement chargé
     *
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
     *
     * @param status Nouveau status du jeu
     */
    public void setStatus(GameStatus status) {
        this.status = status;
        GameServerDispatcher.updateStatus();

        for (LudosPlayer player : Core.get().getPlayerManager().getPlayers()) {
            player.setupScoreboard();
        }

        switch (status) {
            case STARTING -> currentGame.startGame();
            case INGAME -> {
                if (!currentGame.checkIfGameHasToBeEnded()) {
                    currentGame.beginGame();
                }
            }
            case ENDING -> currentGame.endGame();
            case WAITING -> {
                this.unregisterCurrentGame();
                if (defaultGamePluginName != null && autoGameStart) {
                    try {
                        this.loadGame(defaultGamePluginName);
                    } catch (GameStatusException e) {
                        e.printStackTrace();
                    } catch (GameRegisteringException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
