package fr.efreicraft.ludos.core.games.runnables;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import fr.efreicraft.ludos.core.utils.TitleUtils;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Timer de lancement automatique de la partie.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class LobbyCountdown extends BukkitRunnable {

    private final int originalTime;
    private int timer;

    private boolean countingDown = false;

    public LobbyCountdown(int timer) {
        this.timer = timer;
        this.originalTime = timer;
        this.runTaskTimer(Core.get().getPlugin(), 0, 20);
    }

    @Override
    public void run() {
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.WAITING) {
            this.cancel();
            return;
        }
        if(!Core.get().getGameManager().isAutoGameStart()) {
            this.cancel();
            return;
        }
        if(Core.get().getMapManager().getCurrentMap() == null) {
            return;
        }
        if(!Core.get().getMapManager().getCurrentMap().isParsed()) {
            return;
        }

        int players = Core.get().getPlayerManager().getPlayingPlayers().size();
        GameRules rules = Core.get().getGameManager().getCurrentGame().getMetadata().rules();

        if(players < rules.minPlayers() || (!countingDown && players < rules.minPlayersToStart())) {
            timer = originalTime;
            if(countingDown) {
                MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, "&cDémarrage annulé... &7Il n'y a plus assez de joueurs.");
            }
            countingDown = false;
            updateScoreboardOfPlayers();
        } else {
            if(timer == 0) {
                countingDown = false;
                Core.get().getGameManager().setStatus(GameManager.GameStatus.STARTING);
                this.cancel();
            } else {
                countingDown = true;

                if(players >= rules.maxPlayers() * 0.5 && timer > 60) {
                    timer = 60;
                }
                if(players >= rules.maxPlayers() * 0.75 && timer > 30) {
                    timer = 30;
                }
                if(players >= rules.maxPlayers() && timer > 10) {
                    timer = 10;
                }

                if(timer % 60 == 0) {
                    TitleUtils.broadcastTitle("", "&6Début dans &l" + timer / 60 + " minute" + (timer / 60 > 1 ? "s" : ""), 0.5f, 3, 0.25f);
                    SoundUtils.broadcastSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                } else if (timer == 30 || timer == 20 || timer == 10) {
                    TitleUtils.broadcastTitle("", "&6Début dans &l" + timer + " secondes", 0.5f, 2.5f, 0.25f);
                    SoundUtils.broadcastSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                } else if (timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1) {
                    TitleUtils.broadcastTitle("", "&6&l" + timer, 0, 2, 0);
                    SoundUtils.broadcastSound(Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
                }

                updateScoreboardOfPlayers();
                timer--;
            }
        }
    }

    private void updateScoreboardOfPlayers() {
        for(Player player : Core.get().getPlayerManager().getPlayers()) {
            if(countingDown) {
                player.getBoard().setTitle("&f&lDébut dans &e&l" + getTimeString());
            } else {
                player.getBoard().setTitle("&f&lEn attente...");
            }
        }
    }

    public String getTimeString() {
        int minutes = timer / 60;
        int seconds = timer % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

}
