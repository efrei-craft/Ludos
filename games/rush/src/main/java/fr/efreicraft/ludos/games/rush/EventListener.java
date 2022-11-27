package fr.efreicraft.ludos.games.rush;

import com.destroystokyo.paper.entity.villager.ReputationType;
import fr.efreicraft.ludos.core.Core;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

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
            switch (( (TextComponent) villager.customName()).content()) {
                case "Bâtisseur" -> event.getPlayer().openMerchant(logic.merchantBatisseur, true);
                case "Terroriste" -> event.getPlayer().openMerchant(logic.merchantTerroriste, true);
                case "Tavernier" -> event.getPlayer().openMerchant(logic.merchantTavernier, true);
                case "Armurier" -> event.getPlayer().openMerchant(logic.merchantArmurier, true);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {
        if (event.getEntity() instanceof Player) {
            fr.efreicraft.ludos.core.players.Player player = Core.get().getPlayerManager().getPlayer((Player) event.getEntity());
            if (!player.getTeam().isPlayingTeam()) {
                return;
            }
            if (logic.yDeath() >= event.getTo().getY()) {
                player.entity().setHealth(0);
            }
        } else if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void test(VillagerCareerChangeEvent e) {
        getLogger().info(String.valueOf(e.isCancelled()));
        getLogger().info(e.getProfession().name());
//        [01:01:27 INFO]: false
//                [01:01:27 INFO]: NONE
//                [01:01:27 INFO]: false
//                [01:01:27 INFO]: NONE
//                [01:01:27 INFO]: false
//                [01:01:27 INFO]: NONE
//                [01:01:27 INFO]: false
//                [01:01:27 INFO]: NONE
//                [01:01:27 INFO]: false
//                [01:01:27 INFO]: NONE
//                [01:01:27 INFO]: false
//                [01:01:27 INFO]: NONE
//                [01:01:27 INFO]: false
//
    }
}
