package fr.jiveoff.efrei.minigames.games.blockparty.patterns;

import fr.jiveoff.efrei.minigames.core.maps.interfaces.GamePoint;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.List;

/**
 * Classes implementing this interface are used to load or generate a floor pattern for the block party game.
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Minigames/BlockParty
 */
public interface IPatternProvider {
    /**
     * Pattern providers may need to know in advance which blocks are about to be replaced.
     * This function is called during post-map-parse and may be used to initialize the pattern origin.
     * @param points A list of GamePoints in which color pattern will be applied.
     */
    void preparePattern(List<GamePoint> points);

    /**
     * Given a location, returns the correct color block to be placed
     * Expected to be called during post-map-parse to replace gamepoints by actual color blocks
     * @param initialBlockLocation Bukkit Location of the block to be replaced
     * @return A Minecraft Block Data
     */
    BlockData getBlock(Location initialBlockLocation);
}
