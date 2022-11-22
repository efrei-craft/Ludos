package fr.efreicraft.ludos.core.games.runnables;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.games.interfaces.GameTimerAction;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Timer utilisé dans les jeux.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class GameTimer extends BukkitRunnable {

    GameTimerAction action;
    private int time;

    public GameTimer(GameTimerAction action, int time) {
        this.action = action;
        this.time = time;
        this.runTaskTimer(Core.get().getGameManager().getCurrentPlugin(), 0, 20);
    }

    @Override
    public void run() {
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            this.cancel();
            return;
        }
        action.run(time);
        if(time == 0) {
            cancel();
        } else {
            time--;
        }
    }

    public static String getTimeString(int time) {
        int minutes = time / 60;
        int seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}