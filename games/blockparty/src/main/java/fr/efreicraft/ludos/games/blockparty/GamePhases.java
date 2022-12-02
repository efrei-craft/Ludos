package fr.efreicraft.ludos.games.blockparty;

import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.utils.ActionBarUtils;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import fr.efreicraft.ludos.games.blockparty.utils.PlayersUtils;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

/**
 * All phases of the game are defined here.
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Ludos/BlockParty
 */
public class GamePhases {

    private final GameLogic gameLogic;

    private ItemStack selectedBlock;
    private GameTimer phaseTimer;

    public GamePhases(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public void destructor() {
        if (phaseTimer != null) {
            phaseTimer.cancel();
        }
    }

    /**
     * First gameplay phase, where players are free for a few seconds.
     */
    public void beginPreparationPhase() {
        this.selectedBlock = this.gameLogic.getRandomBlockAsItem();

        ActionBarUtils.broadcastActionBar("Préparez vous pour un nouveau tour !");
        int phaseDuration = 4;
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                this.beginDancingPhase();
            }
        }, phaseDuration);
    }
    /**
     * Main gameplay phase, where the game chooses a random block color, and the players have to switch their position
     * to match the block color before the timer ends
     */
    public void beginDancingPhase() {
        PlayersUtils.setItemInMainHandToAll(this.selectedBlock);
        SoundUtils.broadcastSound(Sound.ENTITY_BLAZE_HURT, 1, 0.8f);

        int phaseDuration = 10;
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                this.beginKillPhase();
            } else {
                ActionBarUtils.broadcastActionBar(
                        ">".repeat(remainingTime)
                                + " x "
                                + "<".repeat(remainingTime)
                );
            }
        }, phaseDuration);
    }

    /**
     * Final gameplay phase, where all blocks except the chosen one is deleted.
     */
    public void beginKillPhase() {
        ActionBarUtils.broadcastActionBar("blam !");
        this.gameLogic.clearAllBlocksExcept(this.selectedBlock.getType());
        int phaseDuration = 2;
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                this.endKillPhase();
            }
        }, phaseDuration);
    }

    /**
     * Intermediate phase between two turns, displaying remaining users.
     */
    public void endKillPhase() {
        ActionBarUtils.broadcastActionBar(gameLogic.getRemainingPlayers() + "joueurs restants.");
        int phaseDuration = 2;
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                PlayersUtils.clearAllPlayersInventory();
                this.gameLogic.increaseDifficulty();
                this.gameLogic.generateDanceFloor();
                this.beginPreparationPhase();
            }
        }, phaseDuration);
    }
}
