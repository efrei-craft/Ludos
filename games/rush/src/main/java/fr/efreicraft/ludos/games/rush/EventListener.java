package fr.efreicraft.ludos.games.rush;

import com.destroystokyo.paper.MaterialTags;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Bed;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.VillagerReplenishTradeEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Merchant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

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
        if (MAP_BLOCKS.contains(event.getBlock().getType()) || MaterialTags.BEDS.isTagged(event.getBlock().getType())) {
            event.setDropItems(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        Location mid = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();

        ArrayList<Location> forbiddenGenerators = new ArrayList<>();

        Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM1_GENERATOR").forEach(point -> forbiddenGenerators.add(point.getLocation().toBlockLocation()));
        Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM2_GENERATOR").forEach(point -> forbiddenGenerators.add(point.getLocation().toBlockLocation()));
        Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM3_GENERATOR").forEach(point -> forbiddenGenerators.add(point.getLocation().toBlockLocation()));
        Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM4_GENERATOR").forEach(point -> forbiddenGenerators.add(point.getLocation().toBlockLocation()));

        if (forbiddenGenerators.contains(event.getBlock().getLocation().toBlockLocation())) {
            event.setCancelled(true);
            return;
        }

        Core.get().getMapManager().getCurrentMap().getSpawnPoints().values()
                .forEach(points -> {
                    for (SpawnPoint point : points) {
                        Location block = point.getLocation().clone().toBlockLocation();
                        block.setPitch(0);
                        block.setYaw(0);

                        Location blockAbove = point.getLocation().clone().add(0, 1, 0).toBlockLocation();
                        blockAbove.setPitch(0);
                        blockAbove.setYaw(0);

                        if (block.equals(event.getBlock().getLocation().toBlockLocation()) || blockAbove.equals(event.getBlock().getLocation().toBlockLocation())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                });

        if (Math.abs(event.getBlock().getY() - mid.getBlockY()) > GameLogic.MAX_BUILD_HEIGHT) {
            event.setCancelled(true);
            return;
        }

        if (event.getBlock().getType() != Material.TNT) return;
        fr.efreicraft.ludos.core.players.Player placer = Utils.getLudosPlayer(event.getPlayer());
        if (placer == null) return;

        Location spawnPoint = placer.getTeam().getSpawnPointsForCurrentMap().get(0).getLocation();
        if (spawnPoint.distance(event.getBlock().getLocation()) < GameLogic.NO_TNT_RADIUS) {
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

            Merchant toOpen = null;

            switch (( (TextComponent) villager.customName()).content()) {
                case "Bâtisseur" -> toOpen = logic.merchantBatisseur;
                case "Terroriste" -> toOpen = logic.merchantTerroriste;
                case "Tavernier" -> toOpen = logic.merchantTavernier;
                case "Armurier" -> toOpen = logic.merchantArmurier;
            }

            event.getPlayer().openMerchant(toOpen, false);

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(EntityInteractEvent event) {
        if (MaterialTags.BEDS.isTagged(event.getBlock())) event.setCancelled(true);
    }

    @EventHandler
    public void onEntityMove(EntityMoveEvent event) {

        if (event.getEntity().getType() == EntityType.VILLAGER) {
            event.setTo(event.getFrom());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) return;

        fr.efreicraft.ludos.core.players.Player player = Utils.getLudosPlayer(event.getPlayer());
        if (player == null) return;
        if (!player.getTeam().isPlayingTeam()) {
            return;
        }
        if (logic.yDeath() >= event.getTo().getY() && !player.entity().getGameMode().equals(GameMode.SPECTATOR)) {
            player.entity().setHealth(0);
        }
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
