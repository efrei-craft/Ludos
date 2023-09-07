package fr.efreicraft.ludos.core.maps;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.interfaces.IMapParsedCallback;
import fr.efreicraft.ludos.core.maps.interfaces.MapPoint;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.maps.points.GlobalPoint;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

/**
 * <b>Parseur de cartes</b><br />
 * Permet de récupérer les informations d'une carte grâce à une convention de création de points.<br /><br />
 * <p>
 * Un bloc {@link MapPoint#SPAWN_N_GLOBALPOINT_MARKER} est considéré comme un {@link GlobalPoint} sauf
 * dans le cas que le bloc sous la plaque fait partie de la map {@link ColorUtils#getWoolDyeColorMap()}, ou celui-ci est un {@link SpawnPoint}.<br /><br />
 * <p>
 * Un bloc {@link MapPoint#GAMEPOINT_MARKER} est considéré comme un {@link GamePoint}.<br /><br />
 * <p>
 * Une {@link Material#SPONGE} avec un {@link Material#OAK_SIGN} sont considéré comme les informations de la map. Les
 * deux premières lignes du {@link Material#OAK_SIGN} correspondent au nom de la map, les deux dernières aux auteurs.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class MapParser {

    private MapParser() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Définition des {@link GlobalPoint} de toutes les cartes.
     */
    private static final Map<Material, String> MAP_POINTS = new EnumMap<>(Material.class);

    static {
        MAP_POINTS.put(Material.GOLD_BLOCK, "BOUNDARY");
        MAP_POINTS.put(Material.OBSIDIAN, "MIDDLE");
    }

    /**
     * Permet de déterminer ce que définie le point actuel.<br />
     * <p>
     * Trois types peuvent ressortir :<br />
     * - {@link GlobalPoint} : Le point est un point global à tous les jeux.<br />
     * - {@link SpawnPoint} : Le point est un point de spawn pour une équipe du jeu.<br />
     * - {@link GamePoint} : Le point est un point de jeu.<br />
     *
     * @param block      Bloc actuel
     * @param blockAbove Bloc au-dessus du bloc actuel
     * @param location   Location du bloc actuel
     * @return Un {@link MapPoint} ou null si le bloc n'est pas un point.
     */
    private static MapPoint parseMapPoint(Material block, Material blockAbove, Location location) {
        if (blockAbove == MapPoint.SPAWN_N_GLOBALPOINT_MARKER) {
            if (ColorUtils.getWoolDyeColorMap().containsKey(block)) {
                Team team = Core.get().getTeamManager().getTeamByDyeColor(ColorUtils.getWoolDyeColorMap().get(block));
                if (team != null) {
                    return new SpawnPoint(team, location);
                }
            }
            if (MAP_POINTS.containsKey(block)) {
                return new GlobalPoint(MAP_POINTS.get(block), location);
            }
        } else if (blockAbove == MapPoint.GAMEPOINT_MARKER) {
            Map<Material, String> materialStringMap =
                    Core.get().getGameManager().getCurrentGame().getGamePointsMaterials();
            if (materialStringMap.containsKey(block)) {
                return new GamePoint(materialStringMap.get(block), location);
            }
        }
        return null;
    }

    /**
     * Permet de scanner la carte du monde de façon <b>asynchrone</b> à partir des points donnés.
     *
     * @param world    Monde à scanner
     * @param p1       Point 1 du cuboid de scan
     * @param p2       Point 2 du cuboid de scan
     * @param callback Callback à appeler quand le parsing est terminé.
     * @return Une instance de {@link ParsedMap}. <b>Attention: La carte n'a pas forcément fini de parser étant donné que l'opération est asynchrone. Veuillez utiliser le callback.</b>
     */
    public static ParsedMap parseMap(org.bukkit.World world, Location p1, Location p2, IMapParsedCallback callback) {
        ParsedMap parsedMap = new ParsedMap(world);

        // Load chunks between p1 and p2
        int minX = Math.min(p1.getBlockX(), p2.getBlockX());
        int maxX = Math.max(p1.getBlockX(), p2.getBlockX());
        int minZ = Math.min(p1.getBlockZ(), p2.getBlockZ());
        int maxZ = Math.max(p1.getBlockZ(), p2.getBlockZ());

        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;

        int chunkCount = (maxChunkX - minChunkX + 1) * (maxChunkZ - minChunkZ + 1);
        AtomicInteger chunkRead = new AtomicInteger();

        Set<ChunkSnapshot> chunkSnapshots = new HashSet<>();

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                Chunk chunk = world.getChunkAt(chunkX, chunkZ);
                if (!chunk.isLoaded()) chunk.load();
                chunkSnapshots.add(chunk.getChunkSnapshot(false, false, false));
                chunk.unload();
            }
        }

        List<Block> blocksToBreak = new ArrayList<>();
        for (ChunkSnapshot chunkSnapshot : chunkSnapshots) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
                for (int xz = 0; xz < 16 * 16 * 256; ++xz) {
                    int x = xz & 15;
                    int z = xz >> 4 & 15;
                    int y = xz >> 8;

                    int blockX = chunkSnapshot.getX() * 16 + x;
                    int blockZ = chunkSnapshot.getZ() * 16 + z;

                    Material currentBlock = chunkSnapshot.getBlockType(x, y, z);
                    Material blockAbove = chunkSnapshot.getBlockType(x, y + 1, z);

                    if (currentBlock == Material.SPONGE && blockAbove == Material.OAK_SIGN) {
                        blocksToBreak.add(world.getBlockAt(blockX, y + 1, blockZ));
                        blocksToBreak.add(world.getBlockAt(blockX, y, blockZ));

                        Bukkit.getScheduler().runTask(Core.get().getPlugin(), () -> {
                            Block block = world.getBlockAt(blockX, y + 1, blockZ);
                            Sign sign = (Sign) block.getState();
                            StringBuilder builder = new StringBuilder();
                            ArrayList<TextComponent> lines = new ArrayList<>();
                            for (Component line : sign.lines()) {
                                lines.add((TextComponent) line);
                            }
                            builder.append(lines.get(0).content());
                            parsedMap.setName(builder.toString().trim());
                            builder.delete(0, builder.length());
                            builder.append(lines.get(1).content());
                            parsedMap.setAuthor(builder.toString().trim());
                        });
                    } else if (currentBlock != Material.AIR) {
                        MapPoint mp = parseMapPoint(currentBlock, blockAbove, new Location(world, blockX, y, blockZ));
                        if (mp != null) {
                            parsedMap.addPoint(mp);
                            blocksToBreak.add(world.getBlockAt(blockX, y + 1, blockZ));
                            blocksToBreak.add(world.getBlockAt(blockX, y, blockZ));
                        }
                    }
                }

                chunkRead.getAndIncrement();
            });
        }

        Bukkit.getScheduler().runTaskTimer(Core.get().getPlugin(), task -> {
            if (chunkRead.get() == chunkCount) {
                task.cancel();
                for (Block block : blocksToBreak) {
                    block.setType(Material.AIR);
                }
                parsedMap.setParsed(true);
                parsedMap.setMiddleOfMap(null); // Rappel : passer en paramètre null fait que le point est calculé automatiquement.
                callback.onMapParsed(parsedMap);
            }
        }, 0, 1);

        return parsedMap;
    }

}
