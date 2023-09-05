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

    /**
     * Constructeur du timer.
     * @param action Action à effectuer.
     * @param time Temps initial à décompter (si -1, le timer ne s'arrêtera qu'à la fin du jeu).
     * @param period Période de décompte (en ticks).
     */
    public GameTimer(GameTimerAction action, int time, int period) {
        this.action = action;
        this.time = time;
        this.runTaskTimer(Core.get().getGameManager().getCurrentPlugin(), 0, period);
    }

    /**
     * Constructeur du timer.
     * @param action Action à effectuer.
     * @param time Temps initial à décompter (si -1, le timer ne s'arrêtera qu'à la fin du jeu).
     */
    public GameTimer(GameTimerAction action, int time) {
        this(action, time, 20);
    }

    @Override
    public void run() {
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            this.cancel(); // TODO : peut-être digne d'une exception ça.
            return;
        }
        action.run(Math.abs(time));
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
