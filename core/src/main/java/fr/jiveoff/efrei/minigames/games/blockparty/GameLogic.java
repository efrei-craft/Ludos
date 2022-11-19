package fr.jiveoff.efrei.minigames.games.blockparty;

import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.maps.interfaces.GamePoint;
import fr.jiveoff.efrei.minigames.core.players.Player;
import fr.jiveoff.efrei.minigames.games.blockparty.patterns.IPatternProvider;
import fr.jiveoff.efrei.minigames.games.blockparty.patterns.PikselPattern;
import org.bukkit.Location;

import java.util.List;

/**
 * Classe pour la logique du jeu BlockParty.
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 */
public class GameLogic {

    private int killZonePositionY;
    private final IPatternProvider patternProvider = new PikselPattern();

    /**
     * Given a list of game points, changes the block by the dance floor pattern
     * Expected to run right after map is parsed, to replace game points of type DANCE_FLOOR by the actual pattern
     * @param gamePointList a list of game points to be replaced
     */
    public void generateDanceFloor(List<GamePoint> gamePointList) {
        this.patternProvider.preparePattern(gamePointList);
        for(GamePoint mapPoint : gamePointList) {
            Location location = mapPoint.getLocation();
            Core.getInstance().getMapManager().getCurrentMap().getWorld().setBlockData(
                    location,
                    this.patternProvider.getBlock(location)
            );
        }
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
}
