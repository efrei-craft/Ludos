package fr.efreicraft.ludos.games.arena;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public record EventListener(GameLogic arenaLogic) implements Listener {

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        if(player != null
                && player.getTeam().isPlayingTeam()
        ) {
            Team otherTeam = null;
            for(Map.Entry<String, Team> team : Core.get().getTeamManager().getTeams().entrySet()) {
                if(team.getValue() != player.getTeam() && team.getValue().isPlayingTeam()) {
                    otherTeam = team.getValue();
                    break;
                }
            }
            if(otherTeam != null) {
                arenaLogic.addKill(otherTeam);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }
        Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.getTeam().isPlayingTeam()) {
            return;
        }
        if((Core.get().getMapManager().getCurrentMap().getLowestBoundary().getY() - 5) > event.getTo().getY()) {
            player.entity().setHealth(0);
        }
    }

}
