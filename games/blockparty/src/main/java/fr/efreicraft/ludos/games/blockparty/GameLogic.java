package fr.efreicraft.ludos.games.blockparty;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.games.blockparty.patterns.IPatternProvider;
import fr.efreicraft.ludos.games.blockparty.patterns.SingleRandomBlockPattern;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Classe pour la logique du jeu BlockParty.
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Ludos/BlockParty
 */
public class GameLogic {

    private final GamePhases gamePhases;
    private final IPatternProvider patternProvider = new SingleRandomBlockPattern();

    private List<GamePoint> gamePointList;
    private int killZonePositionY;
    private int difficulty = 0;

    public GameLogic() {
        this.gamePhases = new GamePhases(this);
    }

    /**
     * Called when the game should be unloaded to free some resources
     */
    public void destructor() {
        this.gamePhases.destructor();
    }

    public void setGamePointList() {
        this.gamePointList = Core.get().getMapManager().getCurrentMap().getGamePoints().get("DANCE_FLOOR");
    }

    public void onGameStart() {
        this.gamePhases.beginPreparationPhase();
    }

    public String getRemainingPlayers() {
        return String.valueOf(Core.get().getTeamManager().getTeam("PLAYERS").getPlayers().size());
    }

    public void setupScoreboard(Player player) {
        player.getBoard().clearFields();
        player.getBoard().setField(
                0,
                new ScoreboardField("&b&lJoueurs en vie", true, player1 -> this.getRemainingPlayers())
        );
        player.getBoard().setField(
                1,
                new ScoreboardField("&b&lRound", true,  player1 -> String.valueOf(this.getDifficulty()))
        );
        player.getBoard().setField(
                2,
                new ScoreboardField("&b&lCouleur", this.getSelectedBlockAsString())
        );
    }
    public void refreshScoreboard() {
        for (Player player : Core.get().getPlayerManager().getPlayers()) {
            this.setupScoreboard(player);
        }
    }

    /**
     * Replaces all blocks in the dance-floor by the current pattern.
     */
    public void generateDanceFloor() {
        this.patternProvider.preparePattern(gamePointList);
        for (GamePoint mapPoint : gamePointList) {
            Location location = mapPoint.getLocation();
            Core.get().getMapManager().getCurrentMap().getWorld().setBlockData(
                    location,
                    this.patternProvider.getBlock(location)
            );
        }
    }

    /**
     * Deletes all blocks in the dance-floor, except the block provided in parameter.
     */
    public void clearAllBlocksExcept(Material selectedBlock) {
        for (GamePoint mapPoint : gamePointList) {
            Location location = mapPoint.getLocation();
            BlockData actualBlock = Core.get().getMapManager().getCurrentMap().getWorld().getBlockData(location);
            if (actualBlock.getMaterial().getKey() != selectedBlock.getKey()) {
                Core.get().getMapManager().getCurrentMap().getWorld().setBlockData(
                        location,
                        Material.AIR.createBlockData()
                );
            }
        }
    }

    public void increaseDifficulty() {
        difficulty++;
        this.patternProvider.onDifficultyChange(difficulty);
    }
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Méthode pour définir la position Y de la zone de mort.
     * @param killZoneLocation La position de la zone de mort.
     */
    public void setKillZoneLocation(Location killZoneLocation) {
        this.killZonePositionY = killZoneLocation.getBlockY();
    }

    /**
     * Méthode pour vérifier si le joueur est en dehors de la zone de mort.
      * @param positionY La position Y du joueur.
     * @return True si le joueur est en dehors de la zone de mort.
     */
    public boolean isOutsideKillzone(double positionY) {
        return killZonePositionY > positionY;
    }

    /**
     * Triggers once the player death
     * @param player Player related to that event
     */
    public void onPlayerBelowKillzone(Player player) {
        player.entity().setHealth(0);
    }


    /**
     * Documented in {@link IPatternProvider#getRandomBlockAsItem}
     * Used by {@link GamePhases}
     */
    public ItemStack getRandomBlockAsItem() {
        return this.patternProvider.getRandomBlockAsItem();
    }
    public String getSelectedBlockAsString() {
        Material selectedBlock = this.gamePhases.getSelectedBlock();
        if (selectedBlock == null) {
            return "...";
        }
        return selectedBlock.getKey().asString();
    }
}
