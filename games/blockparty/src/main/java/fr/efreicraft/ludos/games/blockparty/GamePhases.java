package fr.efreicraft.ludos.games.blockparty;

import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.utils.ActionBarUtils;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import fr.efreicraft.ludos.games.blockparty.utils.PlayersUtils;
import org.bukkit.Material;
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
    private int preparationPhaseDuration = 4;
    private int dancingPhaseDuration = 10;
    private int beginKillPhase = 2;
    private int endKillPhase = 2;

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
        if (preparationPhaseDuration > 0) {
            ActionBarUtils.broadcastActionBar("Round " + this.gameLogic.getDifficulty());
            this.phaseTimer = new GameTimer(remainingTime -> {
                if (remainingTime == 0) {
                    this.beginDancingPhase();
                }
            }, preparationPhaseDuration);
        } else {
            this.beginDancingPhase();
        }
    }
    /**
     * Main gameplay phase, where the game chooses a random block color, and the players have to switch their position
     * to match the block color before the timer ends
     */
    public void beginDancingPhase() {
        PlayersUtils.setItemInMainHandToAll(this.selectedBlock);
        SoundUtils.broadcastSound(Sound.ENTITY_BLAZE_HURT, 1, 0.8f);
        this.gameLogic.refreshScoreboard();
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                this.beginKillPhase();
            } else {
                ActionBarUtils.broadcastActionBar(
                          ">".repeat(remainingTime)
                        + " " + remainingTime + " "
                        + "<".repeat(remainingTime)
                );
            }
        }, dancingPhaseDuration);
    }

    /**
     * Final gameplay phase, where all blocks except the chosen one is deleted.
     */
    public void beginKillPhase() {
        this.gameLogic.clearAllBlocksExcept(this.selectedBlock.getType());
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                this.endKillPhase();
            }
        }, beginKillPhase);
    }

    /**
     * Intermediate phase between two turns, displaying remaining users.
     */
    public void endKillPhase() {
        ActionBarUtils.broadcastActionBar(gameLogic.getRemainingPlayers() + " joueurs restants.");
        this.phaseTimer = new GameTimer(remainingTime -> {
            if (remainingTime == 0) {
                PlayersUtils.clearAllPlayersInventory();
                this.increaseDifficulty();
                this.gameLogic.generateDanceFloor();
                this.beginPreparationPhase();
            }
        }, endKillPhase);
    }

    public void increaseDifficulty() {
        this.gameLogic.increaseDifficulty();
        switch (this.gameLogic.getDifficulty()) {
            case 1 -> {
                this.preparationPhaseDuration = 2;
                this.dancingPhaseDuration = 5;
                this.endKillPhase = 1;
            }
            case 2 -> {
                this.preparationPhaseDuration = 1;
                this.dancingPhaseDuration = 4;
                this.endKillPhase = 0;
            }
            case 3 -> {
                this.preparationPhaseDuration = 0;
            }
            case 4 -> {
                this.dancingPhaseDuration = 3;
            }
            case 8 -> {
                this.dancingPhaseDuration = 2;
            }
        }
    }

    public Material getSelectedBlock() {
        if (this.selectedBlock == null) {
            return null;
        }
        return this.selectedBlock.getType();
    }
}
