package fr.efreicraft.ludos.games.blockparty.utils;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores color as key and material as value
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Ludos/BlockParty
 */
public class ColorBlocks {
    private static final Map<String, BlockData> colorToBlockData = new HashMap<>();
    static {
        colorToBlockData.put("black", Material.BLACK_CONCRETE.createBlockData());
        colorToBlockData.put("white", Material.WHITE_CONCRETE.createBlockData());
        colorToBlockData.put("orange", Material.ORANGE_CONCRETE.createBlockData());
        colorToBlockData.put("magenta", Material.MAGENTA_CONCRETE.createBlockData());
        colorToBlockData.put("light_blue", Material.LIGHT_BLUE_CONCRETE.createBlockData());
        colorToBlockData.put("yellow", Material.YELLOW_CONCRETE.createBlockData());
        colorToBlockData.put("lime", Material.LIME_CONCRETE.createBlockData());
        colorToBlockData.put("pink", Material.PINK_CONCRETE.createBlockData());
        colorToBlockData.put("gray", Material.GRAY_CONCRETE.createBlockData());
        colorToBlockData.put("light_gray", Material.LIGHT_GRAY_CONCRETE.createBlockData());
        colorToBlockData.put("cyan", Material.CYAN_CONCRETE.createBlockData());
        colorToBlockData.put("purple", Material.PURPLE_CONCRETE.createBlockData());
        colorToBlockData.put("blue", Material.BLUE_CONCRETE.createBlockData());
        colorToBlockData.put("brown", Material.BROWN_CONCRETE.createBlockData());
        colorToBlockData.put("green", Material.GREEN_CONCRETE.createBlockData());
        colorToBlockData.put("red", Material.RED_CONCRETE.createBlockData());
    }
    public static BlockData get(String color) {
        BlockData result = colorToBlockData.get(color);
        if (result == null) {
            return Material.DIRT.createBlockData();
        }
        return result;
    }
}
