package fr.efreicraft.ludos.games.spleef;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public record EventListener(GameLogic logic) implements Listener {

    @EventHandler
    public void onLeftClickBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

        event.getClickedBlock().breakNaturally();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

    }
}
