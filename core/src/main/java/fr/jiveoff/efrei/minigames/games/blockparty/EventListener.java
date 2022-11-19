package fr.jiveoff.efrei.minigames.games.blockparty;

import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.players.Player;
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
