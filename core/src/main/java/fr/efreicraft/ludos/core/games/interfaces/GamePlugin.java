package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.Core;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Classe abstraite pour les plugins de jeux.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public abstract class GamePlugin extends JavaPlugin {

    protected GamePlugin() {
        Core.get().getLogger().info("Loaded GamePlugin: " + getDescription().getName() + "...");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        Core.get().getGameManager().registerGamePlugin(this);
        Core.get().getLogger().info("Enabling GamePlugin " + getPluginDescription().getName() + ".");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Core.get().getLogger().info("Disabling GamePlugin " + getPluginDescription().getName() + ".");
    }

    public PluginDescriptionFile getPluginDescription() {
        return super.getDescription();
    }

    public abstract Class<? extends Game> getGameClass();

}
