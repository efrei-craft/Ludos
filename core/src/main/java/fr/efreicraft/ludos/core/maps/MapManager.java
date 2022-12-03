package fr.efreicraft.ludos.core.maps;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.maps.exceptions.MapLoadingException;
import fr.efreicraft.ludos.core.utils.SchematicUtils;
import fr.efreicraft.ludos.core.utils.WorldUtils;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

/**
 * Gestionnaire des cartes.<br /><br />
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class MapManager implements IManager {

    /**
     * Point de paste de la carte sur les mondes vides.
     */
    private static final Location SCHEMATIC_FROM = new Location(null, -1, 150, -1);

    /**
     * Carte actuellement chargée
     */
    private ParsedMap currentMap;

    private org.bukkit.World lobbyWorld;

    /**
     * Constructeur du gestionnaire de cartes. Il vérifie que la classe n'est pas déjà initialisée.
     */
    public MapManager() {
        if(Core.get().getMapManager() != null) {
            throw new IllegalStateException("MapManager already initialized !");
        }
    }

    @Override
    public void runManager() {
        setupLobbyWorld();

        int cleanedUpWorlds = WorldUtils.cleanUpWorlds();
        Core.get().getLogger().log(Level.INFO, "Cleaned up {0} worlds.", cleanedUpWorlds);
    }

    /**
     * Récupère les cartes disponibles pour un jeu donné
     * @param game Jeu pour lequel on veut récupérer les cartes
     * @return Liste des cartes disponibles
     */
    public List<String> getMapsForGame(Game game) {
        ArrayList<String> maps = new ArrayList<>();
        File dataFolder = new File(Core.get().getPlugin().getDataFolder(), "schematics/" + game.getMetadata().name());
        if(dataFolder.exists()) {
            for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".schem")) {
                    maps.add(file.getName().replace(".schem", ""));
                }
            }
        }
        return maps;
    }

    /**
     * Décharge la carte actuellement chargée
     */
    public void unloadMap() {
        if (currentMap != null) {
            WorldUtils.deleteWorld(currentMap.getWorld());
            currentMap = null;
        }
    }

    /**
     * Charge le lobby d'attente du serveur.
     */
    private void setupLobbyWorld() {
        String lobbyWorldName = Core.get().getPlugin().getConfig().getString("waitingLobbyName");
        if(lobbyWorldName == null) {
            Core.get().getLogger().log(Level.SEVERE, "Waiting lobby name not set in config !");
            return;
        }

        if(Core.get().getServer().getWorld(lobbyWorldName) != null) {
            lobbyWorld = Core.get().getServer().getWorld(lobbyWorldName);
            return;
        }

        WorldCreator creator = new WorldCreator("WaitingLobby");
        creator.type(WorldType.FLAT);
        creator.generatorSettings("{\"layers\": []}");
        lobbyWorld = creator.createWorld();

        assert lobbyWorld != null;
        lobbyWorld.setSpawnLocation(0, 150, 0);
        WorldUtils.setupClassicWorldGamerules(lobbyWorld);

        Clipboard lobbySchematic;

        try {
            lobbySchematic = SchematicUtils.loadSchematic("Lobby");
            SchematicUtils.pasteSchematic(
                    BukkitAdapter.adapt(lobbyWorld),
                    lobbySchematic,
                    BlockVector3.at(SCHEMATIC_FROM.getX(), SCHEMATIC_FROM.getY(), SCHEMATIC_FROM.getZ())
            );
        } catch (IOException e) {
            Core.get().getLogger().severe("Unable to load the Lobby schematic.");
        }
    }

    /**
     * Charge une carte
     * @param mapName Nom de la carte
     */
    public void loadMap(String mapName) throws MapLoadingException {
        // Première étape : on décharge la carte actuelle
        if(currentMap != null) {
            WorldUtils.deleteWorld(currentMap.getWorld());
        }

        // On créé le nouveau monde vide
        Core.get().getLogger().log(Level.INFO, "Creating world {0}...", mapName);
        org.bukkit.World world = WorldUtils.createWorld(mapName);
        WorldUtils.setupClassicWorldGamerules(world);
        World currentWorld = BukkitAdapter.adapt(world);

        Core.get().getLogger().log(Level.INFO, "Pasting schematic {0} in the new world...", mapName);

        // On récupère la carte à partir du fichier schematic
        Clipboard schematic;

        try {
            schematic = SchematicUtils.loadSchematic(
                    Core.get().getGameManager().getCurrentGame().getMetadata().name() + "/" + mapName
            );
        } catch (IOException e) {
            WorldUtils.deleteWorld(world);
            throw new MapLoadingException("Chargement du schematic impossible.");
        }

        if(schematic == null) {
            WorldUtils.deleteWorld(world);
            throw new MapLoadingException("Chargement du schematic impossible.");
        }

        // On colle le schematic de la nouvelle carte sur le nouveau monde
        try {
            SchematicUtils.pasteSchematic(
                    currentWorld,
                    schematic,
                    BlockVector3.at(SCHEMATIC_FROM.getX(), SCHEMATIC_FROM.getY(), SCHEMATIC_FROM.getZ())
            );
        } catch (WorldEditException e) {
            WorldUtils.deleteWorld(world);
            throw new MapLoadingException("Collage du schematic impossible.");
        }

        Core.get().getLogger().log(Level.INFO, "Schematic pasted, now parsing map...");
        if(Core.get().getGameManager().getCurrentGame() != null) {

            // On récupère le premier BOUNDARY de la map. C'est lui qui définiera le point d'origine de la map.
            Location firstBoundary = WorldUtils.findFirstBoundary(
                    new Location(world, SCHEMATIC_FROM.getX(), SCHEMATIC_FROM.getY(), SCHEMATIC_FROM.getZ()).getBlock()
            );

            if(firstBoundary == null) {
                WorldUtils.deleteWorld(world);
                throw new MapLoadingException("Le premier point de la carte n'a pas été trouvé.");
            }

            // Une carte peut être copiée à un point de direction différent.
            // Grâce au point de base de spawn, on peut déterminer la rotation de la carte.
            BlockVector3 direction = BlockVector3.at(
                    firstBoundary.getX() - SCHEMATIC_FROM.getX(),
                    firstBoundary.getY() - SCHEMATIC_FROM.getY(),
                    firstBoundary.getZ() - SCHEMATIC_FROM.getZ()
            );

            // On récupère le dernier BOUNDARY de la map. On le récupère grâce aux dimensions données par le schematic.
            // On applique également la rotation de la carte pour déterminer le point de fin du cuboid.
            BlockVector3 max = BlockVector3.at(
                    firstBoundary.getX() + schematic.getDimensions().getX() * direction.getX(),
                    firstBoundary.getY() + schematic.getDimensions().getY(),
                    firstBoundary.getZ() + schematic.getDimensions().getZ() * direction.getZ()
            );

            Location lastBoundary = new Location(world, max.getX(), max.getY(), max.getZ());

            // On charge les chunks de la carte
            Set<Chunk> chunks = WorldUtils.getChunksBetween(firstBoundary, lastBoundary);
            for(Chunk chunk : chunks) {
                chunk.load();
            }

            // On applique un point de hook lifecycle pour permettre au jeu de faire des modifications sur la carte ou autre.
            Core.get().getGameManager().getCurrentGame().preMapParse(world);

            // On parse la carte afin de récupérer les différents éléments de la carte.
            currentMap = MapParser.parseMap(
                    currentWorld,
                    firstBoundary,
                    lastBoundary,
                    parsedMap -> {
                        Core.get().getGameManager().getCurrentGame().postMapParse();
                        MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.MAP, "&7La prochaine carte est &b" + currentMap.getName() + "&7!");
                    }
            );
        }
    }

    /**
     * Récupère la carte actuellement chargée
     * @return Carte actuellement chargée
     */
    public ParsedMap getCurrentMap() {
        return currentMap;
    }

    /**
     * Récupère le monde de la salle d'attente
     * @return Monde de la salle d'attente
     */
    public org.bukkit.World getLobbyWorld() {
        return lobbyWorld;
    }

}
