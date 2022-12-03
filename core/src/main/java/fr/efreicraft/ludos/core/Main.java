package fr.efreicraft.ludos.core;

import com.comphenix.protocol.ProtocolLibrary;
import fr.efreicraft.ludos.core.games.GameServerRedisDispatcher;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principale du plugin.
 */
public final class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Saving default config...");
        saveDefaultConfig();

        getLogger().info("Starting Core.");
        new Core(this, ProtocolLibrary.getProtocolManager());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        GameServerRedisDispatcher.bye();
    }

}
