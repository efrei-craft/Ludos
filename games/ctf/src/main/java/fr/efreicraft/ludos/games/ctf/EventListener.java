package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Ewenn BAUDET
 * @param ctfLogic logique du jeu CTF
 */
public record EventListener(GameLogic ctfLogic) implements Listener {
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        LudosPlayer ludosPlayer = Core.get().getPlayerManager().getPlayer(event.getPlayer());

        String teamOfFlag;  //on doit vérifier si le joueur n'appartient pas à cette team avant de casser le drapeau

        //Obtenir la team du drapeau (déplacer ça autre part dans le future ?)
        switch (event.getBlock().getBlockData().getMaterial()) {
            case RED_BANNER -> teamOfFlag = "Rouge";
            case BLUE_BANNER -> teamOfFlag = "Bleu";
            default -> {
                event.setCancelled(true);
                return;
            }
        }

        //gérer le cassage du drapeau
        event.setCancelled(ctfLogic.handleBreakFlag(ludosPlayer, teamOfFlag));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        LudosPlayer ludosPlayer = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        ctfLogic.dropFlagIfCarried(ludosPlayer, true);
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        /*
        LudosPlayer ludosPlayer = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        Location blockLocation = event.getBlock().getLocation();
        ctfLogic.tryToScore(ludosPlayer, blockLocation);
         */
        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if(!event.hasChangedBlock()) return;

        LudosPlayer ludosPlayer = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        Location playerLocation = ludosPlayer.entity().getLocation();
        ctfLogic.tryToScore(ludosPlayer, playerLocation);
    }
}
