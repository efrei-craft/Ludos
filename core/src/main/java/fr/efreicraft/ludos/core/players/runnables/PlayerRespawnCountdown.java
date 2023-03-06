package fr.efreicraft.ludos.core.players.runnables;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.PlayerUtils;
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
    private final LudosPlayer player;

    private final String respawnTitle;
    public PlayerRespawnCountdown(LudosPlayer player) {
        this.player = player;
        this.countdown = Core.get().getGameManager().getCurrentGame().getMetadata().rules().respawnTimer();
        String[] intermediaire = Core.get().getGameManager().getCurrentGame().getMetadata().customData().respawnTitles();
        this.respawnTitle = intermediaire[Core.get()
                .getGameManager().getCurrentGame().getRandom().nextInt(intermediaire.length)];
    }

    @Override
    public void run() {
        if (this.player.entity() == null || this.player.entity().isDead()) {
            this.cancel();
            return;
        }
        if (countdown == 0) {
            this.player.getTeam().spawnPlayer(this.player);
            this.player.entity().clearTitle();
            this.cancel();
        } else {
            String[] toBeDisplayed = this.respawnTitle.split(PlayerUtils.SPLITTER);
            if (toBeDisplayed.length <= 1 || toBeDisplayed[1].isBlank()) {
                this.player.sendTitle(
                        "&c" + this.respawnTitle,
                        "&7Vous réapparaîtrez dans &f" + countdown + " &7secondes.",
                        0,
                        40,
                        0
                );
            } else {
                this.player.sendTitle(
                        "&c" + toBeDisplayed[0],
                        "&7" + toBeDisplayed[1],
                        0,
                        40,
                        0
                );
            }
        }
        countdown--;
    }
}
