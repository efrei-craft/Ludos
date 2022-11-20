package fr.efreicraft.ludos.core.players.runnables;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
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
            this.player.entity().showTitle(Title.title(
                    Component.empty(),
                    Component.empty(),
                    Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
            ));
            this.cancel();
        } else {
            this.player.entity().showTitle(Title.title(
                    Component.text("§cVous êtes mort!"),
                    Component.text("§7Vous réapparaitrez dans §f" + countdown + " §7secondes."),
                    Title.Times.times(Ticks.duration(0), Ticks.duration(40), Ticks.duration(0))
            ));
        }
        countdown--;
    }
}
