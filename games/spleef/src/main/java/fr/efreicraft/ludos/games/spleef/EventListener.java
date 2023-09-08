package fr.efreicraft.ludos.games.spleef;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import io.papermc.paper.entity.LookAnchor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public record EventListener(GameLogic logic) implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isDead()) return;

        switch (event.getCause()) {
            case FALL, SUFFOCATION -> event.setCancelled(true);
            case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> event.setDamage(0);
            case LAVA, VOID -> {
                Core.get().getPlayerManager().getPlayer(player).deathEvent(new PlayerDeathEvent(player, new ArrayList<>(), 0, ""));
                player.setGameMode(org.bukkit.GameMode.SPECTATOR);

                ArrayList<SpawnPoint> points = Core.get().getMapManager().getCurrentMap().getSpawnPoints().get(Core.get().getTeamManager().getTeam("PLAYERS"));
                player.teleport(points.get(new Random().nextInt(points.size())).getLocation());
                player.lookAt(Core.get().getMapManager().getCurrentMap().getMiddleOfMap().toBlockLocation(), LookAnchor.EYES);
                player.setVelocity(new Vector(0, 50, 0));
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onLeftClickBlock(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != GameLogic.getTheShovel().getType()) {
            event.setCancelled(true);
            return;
        }

        Block block = event.getClickedBlock();
        block.breakNaturally();
        if (!block.isValidTool(GameLogic.getTheShovel())) {
            block.getWorld().playSound(block.getLocation(), block.getBlockSoundGroup().getBreakSound(), 0.5f, 1);
        }
    }

    @EventHandler
    public void onSnowballTouch(ProjectileHitEvent event) {
        if (event.getHitEntity() != null) event.setCancelled(true);
    }

    @EventHandler
    public void onSnowballHitBlock(ProjectileHitEvent event) {
        if (event.getHitBlock() == null) return;

        event.getHitBlock().breakNaturally();
    }

    @EventHandler
    public void onInventoryEvent(InventoryInteractEvent event) {
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
    }

    @EventHandler
    public void onSnowballConsume(ProjectileLaunchEvent event) {
        Player player = (Player) event.getEntity().getShooter();
        player.getInventory().setItem(1, new ItemStack(Material.SNOWBALL, 8));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
