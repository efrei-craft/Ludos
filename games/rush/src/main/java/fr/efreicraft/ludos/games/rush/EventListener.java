package fr.efreicraft.ludos.games.rush;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public record EventListener(GameLogic logic) implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        
    }

}
