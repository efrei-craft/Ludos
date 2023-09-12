package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
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
        LudosPlayer ludos_player = Core.get().getPlayerManager().getPlayer(event.getPlayer());

        String team_to_check = "";  //on doit vérifier si le joueur n'appartient pas à cette team avant de casser le drapeau

        switch (event.getBlock().getBlockData().getMaterial()) {
            case RED_BANNER -> team_to_check = "RED";
            case BLUE_BANNER -> team_to_check = "BLUE";
            default -> {
                event.setCancelled(true);
                return;
            }
        }

        //Vérifier si le joueur n'essaie pas de casser le drapeau de sa propre équipe
        if(Core.get().getTeamManager().getTeam(team_to_check).getPlayers().contains(ludos_player)) {
            ludos_player.sendMessage(
                    MessageUtils.ChatPrefix.GAME,
                    "Tu ne peux pas récupérer ton propre drapeau !"
            );
            event.setCancelled(true);
            return;
        }

        ludos_player.sendMessage(
                MessageUtils.ChatPrefix.GAME,
                "Tu as récupéré le drapeau adverse !"
        );

        //TODO : stocker les joueurs possédants les drapeaux dans GameLogic


    }
}
