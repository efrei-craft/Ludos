package fr.efreicraft.ludos.games.blockparty.patterns;

import fr.efreicraft.ludos.core.maps.points.GamePoint;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generates a pattern from an array generated by the Piksel tool.
 * Methods already documented in the interface {@link IPatternProvider}
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Minigames/BlockParty
 */
public class PikselPattern implements IPatternProvider {

    public static final int DEBUGGING_PATTERN_WIDTH = 30;
    public static final int DEBUGGING_PATTERN_HEIGHT = 30;
    protected static final Integer[] DEBUGGING_PATTERN = new Integer[] {
            0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87, 0xff9c3431, 0xff9c3431, 0xff234571, 0xff234571, 0xff146b52, 0xff146b52,
            0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87, 0xff9c3431, 0xff9c3431, 0xff234571, 0xff234571, 0xff146b52, 0xff146b52,
            0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87, 0xff9c3431, 0xff9c3431, 0xff234571, 0xff234571,
            0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87, 0xff9c3431, 0xff9c3431, 0xff234571, 0xff234571,
            0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87, 0xff9c3431, 0xff9c3431,
            0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87, 0xff9c3431, 0xff9c3431,
            0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87,
            0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d, 0xffb62d87, 0xffb62d87,
            0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d,
            0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d, 0xff8e840d, 0xff8e840d,
            0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d,
            0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44, 0xff979d9d, 0xff979d9d,
            0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44,
            0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec, 0xff4e4b44, 0xff4e4b44,
            0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec,
            0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d, 0xffa17fec, 0xffa17fec,
            0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d,
            0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9, 0xff10b76d, 0xff10b76d,
            0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9,
            0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d, 0xff1fc5f9, 0xff1fc5f9,
            0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d,
            0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc, 0xffdeb83d, 0xffdeb83d,
            0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc,
            0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5, 0xffb341bc, 0xffb341bc,
            0xff9c3431, 0xff9c3431, 0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5,
            0xff9c3431, 0xff9c3431, 0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff, 0xff0f7af5, 0xff0f7af5,
            0xff234571, 0xff234571, 0xff9c3431, 0xff9c3431, 0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff,
            0xff234571, 0xff234571, 0xff9c3431, 0xff9c3431, 0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000, 0xffffffff, 0xffffffff,
            0xff146b52, 0xff146b52, 0xff234571, 0xff234571, 0xff9c3431, 0xff9c3431, 0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000,
            0xff146b52, 0xff146b52, 0xff234571, 0xff234571, 0xff9c3431, 0xff9c3431, 0xffb62d87, 0xffb62d87, 0xff8e840d, 0xff8e840d, 0xff979d9d, 0xff979d9d, 0xff4e4b44, 0xff4e4b44, 0xffa17fec, 0xffa17fec, 0xff10b76d, 0xff10b76d, 0xff1fc5f9, 0xff1fc5f9, 0xffdeb83d, 0xffdeb83d, 0xffb341bc, 0xffb341bc, 0xff0f7af5, 0xff0f7af5, 0xff1e26ac, 0xff1e26ac, 0xff000000, 0xff000000
    };

    private int offsetX = -1;
    private int offsetZ = -1;

    private final Map<Integer, BlockData> colorToBlockData = new HashMap<>();

    public PikselPattern() {
        colorToBlockData.put(0xff000000, Material.BLACK_CONCRETE.createBlockData());
        colorToBlockData.put(0xffffffff, Material.WHITE_CONCRETE.createBlockData());
        colorToBlockData.put(0xff0f7af5, Material.ORANGE_CONCRETE.createBlockData());
        colorToBlockData.put(0xffb341bc, Material.MAGENTA_CONCRETE.createBlockData());
        colorToBlockData.put(0xffdeb83d, Material.LIGHT_BLUE_CONCRETE.createBlockData());
        colorToBlockData.put(0xff1fc5f9, Material.YELLOW_CONCRETE.createBlockData());
        colorToBlockData.put(0xff10b76d, Material.LIME_CONCRETE.createBlockData());
        colorToBlockData.put(0xffa17fec, Material.PINK_CONCRETE.createBlockData());
        colorToBlockData.put(0xff4e4b44, Material.GRAY_CONCRETE.createBlockData());
        colorToBlockData.put(0xff979d9d, Material.LIGHT_GRAY_CONCRETE.createBlockData());
        colorToBlockData.put(0xff8e840d, Material.CYAN_CONCRETE.createBlockData());
        colorToBlockData.put(0xffb62d87, Material.PURPLE_CONCRETE.createBlockData());
        colorToBlockData.put(0xff9c3431, Material.BLUE_CONCRETE.createBlockData());
        colorToBlockData.put(0xff234571, Material.BROWN_CONCRETE.createBlockData());
        colorToBlockData.put(0xff146b52, Material.GREEN_CONCRETE.createBlockData());
        colorToBlockData.put(0xff1e26ac, Material.RED_CONCRETE.createBlockData());
    }

    @Override
    public void preparePattern(List<GamePoint> points) {
        for(GamePoint mapPoint : points) {
            Location location = mapPoint.getLocation();
            if (offsetX == -1) {
                offsetX = location.getBlockX();
                offsetZ = location.getBlockZ();
                continue;
            }
            if (location.getBlockX() < offsetX) {
                offsetX = location.getBlockX();
            }
            if (location.getBlockZ() < offsetZ) {
                offsetZ = location.getBlockZ();
            }
        }
    }

    @Override
    public BlockData getBlock(Location initialBlockLocation) {
        int indexX = Math.floorMod(initialBlockLocation.getBlockZ() - offsetZ, DEBUGGING_PATTERN_WIDTH);
        int indexY = DEBUGGING_PATTERN_HEIGHT - Math.floorMod(initialBlockLocation.getBlockX() - offsetX, DEBUGGING_PATTERN_HEIGHT) - 1;
        Integer color = DEBUGGING_PATTERN[indexX + indexY * DEBUGGING_PATTERN_WIDTH];
        BlockData block = colorToBlockData.get(color);
        if (block != null) {
            return block;
        }
        return Material.DIRT.createBlockData();
    }


}