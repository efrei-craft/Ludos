package fr.efreicraft.ludos.games.blockparty.patterns;

import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.games.blockparty.utils.ColorBlocks;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates a pattern from a random set of blocks
 * Methods already documented in the interface {@link IPatternProvider}
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Ludos/BlockParty
 */
public class SingleRandomBlockPattern implements IPatternProvider {
    private final List<String> colors = new ArrayList<>();
    private final Random randomGenerator = new Random();

    @Override
    public void preparePattern(List<GamePoint> points) {
        colors.add("white");
        colors.add("yellow");
        colors.add("light_blue");
    }

    @Override
    public BlockData getBlock(Location initialBlockLocation) {
        int randIndex = randomGenerator.nextInt(colors.size());
        return ColorBlocks.get(colors.get(randIndex));
    }

    public ItemStack getRandomBlockAsItem() {
        int randIndex = randomGenerator.nextInt(colors.size());
        BlockData randBlock = ColorBlocks.get(colors.get(randIndex));
        return new ItemStack(randBlock.getMaterial());
    }
}
