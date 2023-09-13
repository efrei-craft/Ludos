package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public record EventListener(GameLogic ctfLogic) implements Listener {
    @EventHandler
    public void onPlayerDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent event) {
        LudosPlayer ludosPlayer = Core.get().getPlayerManager().getPlayer(event.getPlayer());

        String teamToCheck;  //on doit vérifier si le joueur n'appartient pas à cette team avant de casser le drapeau

        //Obtenir la team du drapeau
        switch (event.getBlock().getBlockData().getMaterial()) {
            case RED_BANNER -> teamToCheck = "RED";
            case BLUE_BANNER -> teamToCheck = "BLUE";
            default -> {
                event.setCancelled(true);
                return;
            }
        }

        //vérifier si un joueur n'essaie pas de casser son propre drapeau,
        //puis stocker le joueur possédant le drapeau dans gameLogic
        event.setCancelled(ctfLogic.BreakFlag(ludosPlayer, teamToCheck));
    }
}
