package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.exceptions.GameRegisteringException;
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
        try {
            Core.get().getGameManager().registerGame(getGameClass());
        } catch (GameRegisteringException e) {
            e.printStackTrace();
        }
        Core.get().getLogger().info("Enabling GamePlugin " + getDescription().getName() + ".");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Core.get().getLogger().info("Disabling GamePlugin " + getDescription().getName() + ".");
    }

    protected abstract Class<? extends Game> getGameClass();

}
