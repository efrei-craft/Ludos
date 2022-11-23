package fr.efreicraft.ludos.games.rush;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public record EventListener(GameLogic logic) implements Listener {

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (event.getBlock().getType() == Material.OBSIDIAN) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onKill(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            Player victim = (Player) event.getEntity();

            // SI le coup est fatal...
            if (victim.getHealth() - event.getFinalDamage() <= 0) {
                logic.handleFinishOffByPlayer((Player) ((EntityDamageByEntityEvent) event).getDamager());
            }
        }
    }

}