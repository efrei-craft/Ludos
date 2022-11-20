package fr.efreicraft.ludos.games.arena;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.players.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

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
                && event.getEntity().getKiller() != null
        ) {
            Player killer = Core.get().getPlayerManager().getPlayer(event.getEntity().getKiller());
            if(killer != null && killer.getTeam().isPlayingTeam()) {
                arenaLogic.addKill(killer.getTeam());
            }
        }
    }

}
