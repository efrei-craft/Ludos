package fr.efreicraft.ludos.games.rush;

import com.destroystokyo.paper.MaterialTags;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import static org.bukkit.Bukkit.getLogger;

public record EventListener(GameLogic logic) implements Listener {

    private static final Set<Material> MAP_BLOCKS = Collections.unmodifiableSet(EnumSet.of(
            Material.OBSIDIAN,
            Material.NETHERITE_BLOCK,
            Material.CHEST,
            Material.BLUE_WALL_BANNER,
            Material.RED_WALL_BANNER,
            Material.LIME_WALL_BANNER,
            Material.YELLOW_WALL_BANNER,
            Material.BLUE_CONCRETE,
            Material.RED_CONCRETE,
            Material.LIME_CONCRETE,
            Material.YELLOW_CONCRETE
    ));

    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (MAP_BLOCKS.contains(event.getBlock().getType()) || event.getBlock().getBlockData().getAsString().contains("bed")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Location mid = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();
        // Math.abs c'est pour mettre une limite en dessous aussi lol
        if (Math.abs(event.getBlock().getY() - mid.getBlockY()) > LudosGame.maxBuildHeight) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() != Material.TNT) return;
        fr.efreicraft.ludos.core.players.Player placer = Utils.getLudosPlayer(event.getPlayer());
        if (placer == null) return;

        Location spawnPoint = placer.getTeam().getSpawnPointsForCurrentMap().get(0).getLocation();
        if (spawnPoint.distance(event.getBlock().getLocation()) < LudosGame.noTNTRadius) {
            event.setCancelled(true);
            placer.sendMessage(MessageUtils.ChatPrefix.GAME, "&cVous ne pouvez pas placer de TNT aussi près de votre spawn !");
        }
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> MAP_BLOCKS.contains(block.getType()));

        for (Block block : event.blockList()) {
            if (!MaterialTags.BEDS.isTagged(block.getType())) continue;

            for (Team team : Core.get().getTeamManager().getTeams().values()) {
                if (((Bed) block.getState()).getColor() == team.getColor().dyeColor()) {
                    logic.bedDestroyed(team, true);
                }
            }
        }
    }

    @EventHandler
    public void onInteractVillager(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().isInvulnerable()) {
            if (event.getRightClicked().getType() != EntityType.VILLAGER) return;

            Villager villager = (Villager) event.getRightClicked();
            villager.clearReputations();
            if (villager.customName() == null) return;

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
    public void onInteract(EntityInteractEvent event) {
        if (MaterialTags.BEDS.isTagged(event.getBlock())) event.setCancelled(true);
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

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

    }

    @EventHandler
    public void onTradeChange(VillagerReplenishTradeEvent event) {
        event.getRecipe().setDemand(-1);
        event.getRecipe().setPriceMultiplier(1);
        event.getRecipe().setSpecialPrice(0);
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        // Nooo mooore crafting!
        event.setCancelled(true);
    }
}
