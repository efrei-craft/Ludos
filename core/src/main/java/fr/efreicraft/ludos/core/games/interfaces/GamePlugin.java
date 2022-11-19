package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.Core;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public abstract class GamePlugin extends JavaPlugin {

    protected GamePlugin() {
        Core.getInstance().getGameManager().registerGame(getGameClass());
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Core.getInstance().getLogger().info("Starting " + getDescription().getName() + ".");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Core.getInstance().getLogger().info("Stopping " + getDescription().getName() + ".");
    }

    protected abstract Class<? extends Game> getGameClass();

}
