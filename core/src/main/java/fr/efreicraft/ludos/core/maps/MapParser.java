package fr.efreicraft.ludos.core.maps;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.interfaces.*;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.maps.points.GlobalPoint;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.util.*;
import java.util.logging.Level;

/**
 * <b>Parseur de cartes</b><br />
 * Permet de récupérer les informations d'une carte grâce à une convention de création de points.<br /><br />
 *
 * Un bloc avec une plaque de pression {@link Material#HEAVY_WEIGHTED_PRESSURE_PLATE} est considérée comme un {@link GlobalPoint} sauf
 * dans le cas que le bloc sous la plaque fait partie de la map {@link ColorUtils#getDyeColorMap()}, ou celui-ci est un {@link SpawnPoint}.<br /><br />
 *
 * Un bloc avec une plaque de pression {@link Material#LIGHT_WEIGHTED_PRESSURE_PLATE} est considérée comme un {@link GamePoint}.<br /><br />
 *
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
    }

    /**
     * Permet de déterminer ce que définie le point actuel.<br />
     *
     * Trois types peuvent ressortir :<br />
     * - {@link GlobalPoint} : Le point est un point global à tous les jeux.<br />
     * - {@link SpawnPoint} : Le point est un point de spawn pour une équipe du jeu.<br />
     * - {@link GamePoint} : Le point est un point de jeu.<br />
     *
     * @param block Bloc actuel
     * @param blockAbove Bloc au dessus du bloc actuel
     * @param location Location du bloc actuel
     * @return Un {@link MapPoint} ou null si le bloc n'est pas un point.
     */
    private static MapPoint parseMapPoint(Material block, Material blockAbove, Location location) {
        if (blockAbove == Material.HEAVY_WEIGHTED_PRESSURE_PLATE) {
            if(ColorUtils.getDyeColorMap().containsKey(block)) {
                Team team = Core.get().getTeamManager().getTeamByDyeColor(ColorUtils.getDyeColorMap().get(block));
                if(team != null) {
                    return new SpawnPoint(team, location);
                }
            }
            if(MAP_POINTS.containsKey(block)) {
                return new GlobalPoint(MAP_POINTS.get(block), location);
            }
        } else if (blockAbove == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
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
     * @param world Monde à scanner
     * @param p1 Point 1 du cuboid de scan
     * @param p2 Point 2 du cuboid de scan
     * @param callback Callback à appeler quand le parsing est terminé.
     * @return Une instance de {@link ParsedMap}. <b>Attention: La carte n'a pas forcément fini de parser étant donné que l'opération est asynchrone. Veuillez utiliser le callback.</b>
     */
    public static ParsedMap parseMap(World world, Location p1, Location p2, IMapParsedCallback callback) {
        Region region = new CuboidRegion(
                world,
                BlockVector3.at(p1.getX(), p1.getY(), p1.getZ()),
                BlockVector3.at(p2.getX(), p2.getY(), p2.getZ())
        );

        Core.get().getLogger().log(Level.INFO, "Parsing map: {0} in world: {1}", new Object[]{ region, world.getName() });

        org.bukkit.World bukkitWorld = BukkitAdapter.adapt(world);

        ParsedMap parsedMap = new ParsedMap(bukkitWorld);

        Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
            Iterator<BlockVector3> iterator = region.iterator();
            List<Block> blocksToBreak = new ArrayList<>();

            while (iterator.hasNext()) {
                BlockVector3 currentBlockVector = iterator.next();
                Block currentBlock = bukkitWorld.getBlockAt(
                        currentBlockVector.getX(),
                        currentBlockVector.getY(),
                        currentBlockVector.getZ()
                );
                Block blockAbove = bukkitWorld.getBlockAt(
                        currentBlockVector.getX(),
                        currentBlockVector.getY() + 1,
                        currentBlockVector.getZ()
                );

                if (currentBlock.getType() == Material.SPONGE && blockAbove.getType() == Material.OAK_SIGN) {
                    Bukkit.getScheduler().runTask(Core.get().getPlugin(), () -> {
                        Sign sign = (Sign) blockAbove.getState();
                        StringBuilder builder = new StringBuilder();
                        ArrayList<TextComponent> lines = new ArrayList<>();
                        for (Component line : sign.lines()) {
                            lines.add((TextComponent) line);
                        }
                        builder.append(lines.get(0).content()).append(lines.get(1).content());
                        parsedMap.setName(builder.toString().trim());
                        builder.delete(0, builder.length());
                        builder.append(lines.get(2).content()).append(lines.get(3).content());
                        parsedMap.setAuthor(builder.toString().trim());
                    });

                    blocksToBreak.add(blockAbove);
                    blocksToBreak.add(currentBlock);
                } else if (currentBlock.getType() != Material.AIR) {
                    MapPoint mp = parseMapPoint(currentBlock.getType(), blockAbove.getType(), currentBlock.getLocation());
                    if (mp != null) {
                        parsedMap.addPoint(mp);
                        blocksToBreak.add(blockAbove);
                        blocksToBreak.add(currentBlock);
                    }
                }
            }

            Bukkit.getScheduler().runTask(Core.get().getPlugin(), () -> {
                for (Block block : blocksToBreak) {
                    block.setType(Material.AIR);
                }
                parsedMap.setParsed(true);
                parsedMap.setMiddleOfMap(null);
                callback.onMapParsed(parsedMap);
            });
        });

        return parsedMap;
    }

}
