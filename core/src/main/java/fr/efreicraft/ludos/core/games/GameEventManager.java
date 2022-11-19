package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.interfaces.IGame;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.logging.Level;

/**
 * Utility class to delegate events registering and unregistering for a loaded game.
 */
public class GameEventManager {
    private final IGame minigame;

    public GameEventManager(IGame minigame) {
        this.minigame = minigame;
    }

    /**
     * Expected to be called on the {@link Game#startGame} lifecycle, this methods retrieve the game's event listener,
     * and if set, registers it to the bukkit server.
     */
    public void registerMinigameEvents() {
        Listener eventListenerInstance = minigame.getEventListener();
        if (eventListenerInstance != null) {
            Core.getInstance().getServer().getPluginManager()
                .registerEvents(eventListenerInstance, Core.getInstance().getPlugin());
        } else {
            String gameName = Core.getInstance().getGameManager().getCurrentGame().getMetadata().getClass().getName();
            Core.getInstance().getLogger().log(Level.WARNING, "[GameEventManager] {0} has no event listener set.", gameName);
        }
    }

    /**
     * Unload the game's event listener from the bukkit server.
     */
    public void unregisterMinigameEvents() {
        Listener eventListenerInstance = minigame.getEventListener();
        if (eventListenerInstance != null) {
            HandlerList.unregisterAll(eventListenerInstance);
        }
    }
}
