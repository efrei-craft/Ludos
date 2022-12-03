package fr.efreicraft.ludos.games.blockparty.patterns;

import fr.efreicraft.ludos.core.maps.points.GamePoint;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Classes implementing this interface are used to load or generate a floor pattern for the block party game.
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Ludos/BlockParty
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

    /**
     * The blockparty concists of a floor of colored blocks, and at a given interval, remove all colors except one.
     * As the pattern haves the control on which colors can be displayed, it's this class' duty to provide a way to get
     * a random block.
     * @return An item that represents a block that can be placed by the pattern.
     */
    ItemStack getRandomBlockAsItem();
}
