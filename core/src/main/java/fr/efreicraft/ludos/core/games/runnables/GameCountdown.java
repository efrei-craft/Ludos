package fr.efreicraft.ludos.core.games.runnables;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import fr.efreicraft.ludos.core.utils.TitleUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Décompte avant le début de la partie.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class GameCountdown extends BukkitRunnable {

    /**
     * Temps restant avant le début de la partie.
     */
    private int countdown = 10;

    @Override
    public void run() {
        if (Core.get().getGameManager().getCurrentGame() == null) {
            Bukkit.getLogger().warning("La tache de countdown de début de partie continue à être exécutée malgré qu'on soit pas en jeu. Cela peut arriver si vous faites /game reset pendant ledit countdown. Annulation de la tache...");
            this.cancel();
            return;
        }
        if (countdown == 10) {
            SoundUtils.broadcastSound(Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        } else if (countdown == 5 || countdown == 4 || countdown == 3 || countdown == 2 || countdown == 1) {
            TitleUtils.broadcastTitle(Core.get().getGameManager().getCurrentGame().getMetadata().color() + countdown, "", 0, 2, 0);
            SoundUtils.broadcastSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
        } else if (countdown == 0) {
            SoundUtils.broadcastSound(Sound.BLOCK_NOTE_BLOCK_PLING, 1, 2);
            TitleUtils.broadcastTitle(Core.get().getGameManager().getCurrentGame().getMetadata().color() + "GO", "&7Que la partie commence !", 0, 2, 0.5f);
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, "&7Que la partie commence !");
            Core.get().getGameManager().setStatus(GameManager.GameStatus.INGAME);
            this.cancel();
        }
        countdown--;
    }
}
