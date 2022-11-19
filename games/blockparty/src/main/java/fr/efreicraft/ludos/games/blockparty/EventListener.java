package fr.efreicraft.ludos.games.blockparty;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Event Listener record for BlockParty minigame.
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Minigames/BlockParty
 */
public record EventListener(GameLogic blockParty) implements Listener {

    /**
     * Evenement de mouvement du joueur pour vérifier s'il est en dehors de la zone de mort.
     * @param event Evenement de mouvement du joueur.
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }
        Player player = Core.getInstance().getPlayerManager().getPlayer(event.getPlayer());
        if (!player.getTeam().isPlayingTeam()) {
            return;
        }
        if (this.blockParty.isOutsideKillzone(event.getTo().getY())) {
            blockParty.onPlayerBelowKillzone(player);
        }
    }
}
