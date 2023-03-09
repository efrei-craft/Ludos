package fr.efreicraft.ludos.core;

import com.comphenix.protocol.ProtocolLibrary;
import fr.efreicraft.ACP.ACP;
import fr.efreicraft.ludos.core.games.GameServerDispatcher;
import fr.efreicraft.ludos.core.handlers.RedisHandler;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe principale du plugin.
 */
public final class LudosCore extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Saving default config...");
        saveDefaultConfig();

        getLogger().info("Starting Core.");
        new Core(this, ProtocolLibrary.getProtocolManager());

        ACP.getClient().getRedis().addHandler(new RedisHandler());
    }

}
