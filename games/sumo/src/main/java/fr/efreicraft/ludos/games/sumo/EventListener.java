package fr.efreicraft.ludos.games.sumo;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public record EventListener(GameLogic sumo) implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }
        LudosPlayer player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.getTeam().isPlayingTeam()) {
            return;
        }
        if (this.sumo.isOutsideKillzone(event.getTo().getY())) {
            sumo.onPlayerBelowKillzone(player);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof org.bukkit.entity.Player bukkitPlayer) {
            LudosPlayer player = Core.get().getPlayerManager().getPlayer(bukkitPlayer);
            if (!player.getTeam().isPlayingTeam()) {
                return;
            }
            event.setDamage(0);
        }
    }
}
