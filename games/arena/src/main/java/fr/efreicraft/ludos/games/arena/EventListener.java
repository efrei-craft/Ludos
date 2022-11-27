package fr.efreicraft.ludos.games.arena;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.Map;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public record EventListener(GameLogic arenaLogic) implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        if(
                player != null
                && player.getTeam().isPlayingTeam()
        ) {
            arenaLogic.resetKillstreak(player);
            Team otherTeam = null;
            for(Map.Entry<String, Team> team : Core.get().getTeamManager().getTeams().entrySet()) {
                if(team.getValue() != player.getTeam() && team.getValue().isPlayingTeam()) {
                    Player killer = Core.get().getPlayerManager().getPlayer(event.getEntity().getKiller());
                    if(killer != null && killer.getTeam() == team.getValue()) {
                        arenaLogic.addPlayerKill(killer);
                    }
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
        if(player.entity().getGameMode() == GameMode.ADVENTURE
                && (Core.get().getMapManager().getCurrentMap().getLowestBoundary().getY() - 5) > event.getTo().getY()) {
            player.entity().setHealth(0);
        }
    }

}
