package fr.efreicraft.ludos.core.players;

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
    private int countdown = 5;

    /**
     * Joueur concerné par le décompte.
     */
    private final Player player;

    PlayerRespawnCountdown(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
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
