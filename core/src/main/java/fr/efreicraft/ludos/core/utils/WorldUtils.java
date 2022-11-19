package fr.efreicraft.ludos.core.utils;

import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.Core;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Utilitaire de monde.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class WorldUtils {

    /**
     * Rayon de recherche du premier boundary de map.
     */
    private static final Integer FIND_FIRST_BOUNDARY_RADIUS = 10;

    /**
     * Valeur de fallback pour le préfixe des noms normalisés de monde.
     */
    private static String worldPrefix = "MG_";

    static {
        // Chargement du préfixe des noms normalisés de monde depuis la configuration.
        String prefixFromConfig = Core.getInstance().getPlugin().getConfig().getString("mapPrefix");
        if(prefixFromConfig != null && !prefixFromConfig.isEmpty()) {
            worldPrefix = prefixFromConfig;
        }
    }

    private WorldUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Nettoie les mondes créés par le plugin.
     * @return Nombre de mondes supprimés
     */
    public static int cleanUpWorlds() {
        List<World> worldsToCleanUp = Bukkit.getWorlds()
                .stream().filter(world -> world.getName().startsWith(worldPrefix)).toList();
        for(World world : worldsToCleanUp) {
            deleteWorld(world);
        }
        return worldsToCleanUp.size();
    }

    /**
     * Supprime un {@link org.bukkit.World} et ses fichiers.
     * @param world Monde à supprimer.
     */
    public static void deleteWorld(org.bukkit.World world) {
        for(org.bukkit.entity.Player player : world.getPlayers()) {
            Player p = Core.getInstance().getPlayerManager().getPlayer(player);
            if(p == null) {
                player.teleport(Core.getInstance().getMapManager().getLobbyWorld().getSpawnLocation());
            } else {
                p.spawnAtWaitingLobby();
            }
        }
        Bukkit.unloadWorld(world, false);
        try {
            Core.getInstance().getLogger().info("Deleting world folder: " + world.getWorldFolder().getAbsolutePath());
            FileUtils.deleteDirectory(world.getWorldFolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Créée un nouveau {@link org.bukkit.World} avec le nom spécifié.
     * @param name Nom du monde.
     * @return Monde créé.
     */
    public static org.bukkit.World createWorld(String name) {
        String normalizedWorldName = getNormalizedWorldName(name);
        org.bukkit.World world = Core.getInstance().getServer().getWorld(normalizedWorldName);
        if(world != null) {
            deleteWorld(world);
        }
        WorldCreator creator = new WorldCreator(normalizedWorldName);
        creator.type(WorldType.FLAT);
        creator.generatorSettings("{\"layers\": []}");
        creator.generateStructures(false);
        return creator.createWorld();
    }

    /**
     * Récupère le nom normalisé d'un monde.
     * @param name Nom du monde.
     * @return Nom normalisé du monde.
     */
    private static String getNormalizedWorldName(String name) {
        return worldPrefix + name;
    }

    /**
     * Récupère tous les chunks entre deux positions.
     * @param location1 Position 1.
     * @param location2 Position 2.
     * @return Un set de chunks.
     */
    public static Set<Chunk> getChunksBetween(Location location1, Location location2) {
        Set<Chunk> chunks = new HashSet<>();
        int minX = Math.min(location1.getBlockX(), location2.getBlockX());
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                Chunk chunk = new Location(location1.getWorld(), x, 0, z).getChunk();
                if (!chunk.isLoaded())
                    chunk.load();
                chunks.add(chunk);
            }
        }
        return chunks;
    }

    /**
     * Récupère le premier BOUNDARY trouvé dans un rayon de {@link #FIND_FIRST_BOUNDARY_RADIUS}.
     * @param start Position de départ de la recherche.
     * @return Position du premier BOUNDARY trouvé.
     */
    public static Location findFirstBoundary(Block start){
        for(double x = start.getLocation().getX() - FIND_FIRST_BOUNDARY_RADIUS; x <= start.getLocation().getX() + FIND_FIRST_BOUNDARY_RADIUS; x++){
            for(double y = start.getLocation().getY() - FIND_FIRST_BOUNDARY_RADIUS; y <= start.getLocation().getY() + FIND_FIRST_BOUNDARY_RADIUS; y++){
                for(double z = start.getLocation().getZ() - FIND_FIRST_BOUNDARY_RADIUS; z <= start.getLocation().getZ() + FIND_FIRST_BOUNDARY_RADIUS; z++){
                    Block block = start.getWorld().getBlockAt(new Location(start.getWorld(), x, y, z));
                    Block blockUp = start.getWorld().getBlockAt(new Location(start.getWorld(), x, y + 1, z));
                    if(block.getType() == Material.GOLD_BLOCK && blockUp.getType() == Material.HEAVY_WEIGHTED_PRESSURE_PLATE){
                        return block.getLocation();
                    }
                }
            }
        }
        return null;
    }

    public static void setupClassicWorldGamerules(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.DO_LIMITED_CRAFTING, true);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, false);
        world.setGameRule(GameRule.REDUCED_DEBUG_INFO, true);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
    }

}
