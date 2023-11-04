package fr.efreicraft.ludos.games.dac;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public record EventListener(GameLogic dac) implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }
        LudosPlayer player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.getTeam().isPlayingTeam()) {
            return;
        }
        if (this.dac.isInBassin(event.getTo())) {
            dac.onPlayerInBassin(player);
        } else if (this.dac.isOnGround(event.getTo().getY())) {
            dac.onPlayerTouchingGround(player);
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
