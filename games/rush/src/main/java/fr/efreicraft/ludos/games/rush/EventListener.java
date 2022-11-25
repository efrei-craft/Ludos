package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record EventListener(GameLogic logic) implements Listener {

    private static final Set<Material> MAP_BLOCKS = Collections.unmodifiableSet(EnumSet.of(
            Material.OBSIDIAN,
            Material.NETHERITE_BLOCK
    ));

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (MAP_BLOCKS.contains(event.getBlock().getType()) || event.getBlock().getBlockData().getAsString().contains("bed")) {
            event.setCancelled(true);
        }
    }

//    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            Player victim = (Player) event.getEntity();

            // SI le coup est fatal...
            if (victim.getHealth() - event.getFinalDamage() <= 0) {
                logic.handleFinishOffByPlayer((Player) ((EntityDamageByEntityEvent) event).getDamager());
            }
        }
    }

    @EventHandler
    public void onInteractVillager(PlayerInteractEntityEvent event) {
        // Note : à changer si on ajoute une autre entité invulnérable :)
        if (event.getRightClicked().isInvulnerable()) {
            event.getPlayer().openMerchant(logic.merchantBatisseur, true);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        fr.efreicraft.ludos.core.players.Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.getTeam().isPlayingTeam()) {
            return;
        }
        if(logic.yDeath() >= event.getTo().getY()) {
            player.entity().setHealth(0);
        }
    }
}
