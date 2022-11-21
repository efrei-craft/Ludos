package fr.efreicraft.ludos.core.players.runnables;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Décompte avant le début de la partie.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class PlayerRespawnCountdown extends BukkitRunnable {

    /**
     * Temps restant avant le début de la partie.
     */
    private int countdown;

    /**
     * Joueur concerné par le décompte.
     */
    private final Player player;

    public PlayerRespawnCountdown(Player player) {
        this.player = player;
        countdown = Core.get().getGameManager().getCurrentGame().getMetadata().rules().respawnTimer();
    }

    @Override
    public void run() {
        if (this.player.entity() == null || this.player.entity().isDead()) {
            this.cancel();
            return;
        }
        if (countdown == 0) {
            this.player.getTeam().spawnPlayer(this.player);
            this.player.sendTitle("", "", 0, 40, 0);
            this.cancel();
        } else {
            this.player.sendTitle(
                    "&cVous êtes mort!",
                    "&7Vous réapparaîtrez dans &f" + countdown + " &7secondes.",
                    0,
                    40,
                    0
            );
        }
        countdown--;
    }
}
