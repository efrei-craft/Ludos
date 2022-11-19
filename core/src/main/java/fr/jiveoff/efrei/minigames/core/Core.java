package fr.jiveoff.efrei.minigames.core;

import com.comphenix.protocol.ProtocolManager;
import fr.jiveoff.efrei.minigames.core.commands.CommandManager;
import fr.jiveoff.efrei.minigames.core.games.GameManager;
import fr.jiveoff.efrei.minigames.core.maps.MapManager;
import fr.jiveoff.efrei.minigames.core.players.PlayerManager;
import fr.jiveoff.efrei.minigames.core.teams.TeamManager;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.logging.Logger;

/**
 * MG Framework Core
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class Core {

    /**
     * Singleton de la classe Core.
     */
    private static Core instance;

    /**
     * Plugin Java Bukkit pour interfacer avec l'API Bukkit.
     */
    private final JavaPlugin plugin;

    /**
     * ProtocolManager pour interfacer avec l'API ProtocolLib pour la manipulation des packets.
     */
    private final ProtocolManager protocolManager;

    /**
     * CommandManager pour gérer les commandes.
     */
    private CommandManager commandManager;

    /**
     * GameManager pour gérer les jeux.
     */
    private GameManager gameManager;

    /**
     * MapManager pour gérer les maps.
     */
    private MapManager mapManager;

    /**
     * TeamManager pour gérer les équipes.
     */
    private TeamManager teamManager;

    /**
     * PlayerManager pour gérer les joueurs.
     */
    private PlayerManager playerManager;

    /**
     * Constructeur de la classe Core.
     * @param plugin Plugin Java Bukkit pour interfacer avec l'API Bukkit.
     * @param protocolManager ProtocolManager pour interfacer avec l'API ProtocolLib pour la manipulation des packets.
     */
    public Core(JavaPlugin plugin, ProtocolManager protocolManager) {
        setInstance(this);
        this.protocolManager = protocolManager;
        plugin.getLogger().info("Loading core...");
        this.plugin = plugin;
        this.loadManagers();
        this.registerEvents();
    }

    /**
     * Charge les managers.
     */
    private void loadManagers() {
        plugin.getLogger().info("Loading managers...");
        commandManager = new CommandManager();
        mapManager = new MapManager();
        playerManager = new PlayerManager();
        teamManager = new TeamManager();
        gameManager = new GameManager();

        commandManager.runManager();
        mapManager.runManager();
        playerManager.runManager();
        teamManager.runManager();
        gameManager.runManager();
    }

    /**
     * Enregistre les event listener de Core.
     */
    private void registerEvents() {
        plugin.getLogger().info("Registering events...");
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
    }

    /**
     * Retourne l'instance du CommandManager.
     * @return Gestionnaire des commandes.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

    /**
     * Retourne l'instance du MapManager.
     * @return Gestionnaire des cartes.
     */
    public MapManager getMapManager() {
        return mapManager;
    }

    /**
     * Retourne l'instance du GameManager.
     * @return Gestionnaire des jeux.
     */
    public GameManager getGameManager() {
        return gameManager;
    }

    /**
     * Retourne l'instance du TeamManager.
     * @return Gestionnaire des équipes.
     */
    public TeamManager getTeamManager() {
        return teamManager;
    }

    /**
     * Retourne l'instance du PlayerManager.
     * @return Gestionnaire des joueurs.
     */
    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    /**
     * Retourne l'instance du {@link JavaPlugin} Minecraft.
     * @return Plugin Java Bukkit pour interfacer avec l'API Bukkit.
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * Retourne l'instance du singleton de la classe Core.
     * @return Singleton de la classe Core.
     */
    public static Core getInstance() {
        return instance;
    }

    /**
     * Définit l'instance du singleton de la classe Core.
     * @param instance Singleton de la classe Core.
     */
    private static void setInstance(Core instance) {
        Core.instance = instance;
    }

    /**
     * Récupération du {@link Logger} du plugin.
     * @return Logger du plugin.
     */
    public Logger getLogger() {
        return plugin.getLogger();
    }

    /**
     * Récupération du {@link Server}.
     * @return Serveur Minecraft.
     */
    public Server getServer() {
        return plugin.getServer();
    }

    /**
     * Récupération du {@link ScoreboardManager} (natif au serveur Minecraft).
     * @return Gestionnaire des scoreboards natif de Minecraft.
     */
    public ScoreboardManager getScoreboardManager() {
        return this.getServer().getScoreboardManager();
    }

    /**
     * Récupération du {@link ProtocolManager} (API ProtocolLib).
     * @return Gestionnaire des packets grâce à ProtocolLib.
     */
    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }

}
