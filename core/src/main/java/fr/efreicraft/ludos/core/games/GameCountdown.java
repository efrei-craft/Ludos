package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Décompte avant le début de la partie.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GameCountdown extends BukkitRunnable {

    /**
     * Temps restant avant le début de la partie.
     */
    private int countdown = 10;

    @Override
    public void run() {
        if (countdown == 10 || countdown == 5 || countdown == 4 || countdown == 3 || countdown == 2 || countdown == 1) {
            MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&7Le jeu commence dans &f" + countdown + " &7secondes!");
        }
        if (countdown == 0) {
            MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&7Le jeu commence maintenant!");
            Core.getInstance().getGameManager().setStatus(GameManager.GameStatus.INGAME);
            this.cancel();
        }
        countdown--;
    }
}
