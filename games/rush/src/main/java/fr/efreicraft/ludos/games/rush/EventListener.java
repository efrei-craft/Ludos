package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import io.papermc.paper.event.entity.EntityMoveEvent;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
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
//    public void onDamage(EntityDamageEvent event) {
//        if (event instanceof EntityDamageByEntityEvent) {
//            Player victim = (Player) event.getEntity();
//
//            // SI le coup est fatal...
//            if (victim.getHealth() - event.getFinalDamage() <= 0) {
//                logic.handleFinishOffByPlayer((Player) ((EntityDamageByEntityEvent) event).getDamager());
//            }
//        }
//    }

    @EventHandler
    public void onInteractVillager(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().isInvulnerable()) {
            if (event.getRightClicked().getType() != EntityType.VILLAGER) return;

            Villager villager = (Villager) event.getRightClicked();
            switch (villager.getProfession()) {
                case TOOLSMITH -> event.getPlayer().openMerchant(logic.merchantBatisseur, true);
                case WEAPONSMITH -> event.getPlayer().openMerchant(logic.merchantTerroriste, true);
                case BUTCHER -> event.getPlayer().openMerchant(logic.merchantTavernier, true);
                case MASON -> event.getPlayer().openMerchant(logic.merchantArmurier, true);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerMove(EntityMoveEvent event) {
        if (event.getEntity() instanceof Player) {
            fr.efreicraft.ludos.core.players.Player player = Core.get().getPlayerManager().getPlayer((Player) event.getEntity());
            if (!player.getTeam().isPlayingTeam()) {
                return;
            }
            if (logic.yDeath() >= event.getTo().getY()) {
                player.entity().setHealth(0);
            }
        } else if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setCancelled(true);
        }
    }
}
