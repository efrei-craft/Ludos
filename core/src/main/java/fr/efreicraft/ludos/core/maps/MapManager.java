package fr.efreicraft.ludos.core.maps;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.maps.exceptions.MapLoadingException;
import fr.efreicraft.ludos.core.maps.interfaces.MapTypes;
import fr.efreicraft.ludos.core.utils.SchematicUtils;
import fr.efreicraft.ludos.core.utils.WorldUtils;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

/**
 * Gestionnaire des cartes.<br /><br />
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @author Idir NM. {@literal <idir.nait-meddour@efrei.net>}
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
     * Une liste des cartes du jeu actuellement chargé accompagnées de leur type.
     */
    private final Map<String, MapTypes> currentGameMaps = new HashMap<>();

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
     * Répertorie les cartes disponibles d'un jeu, accompagnées de leur type.
     * Cette fonction est appelée à chaque changement de jeu.
     * @param game Jeu pour lequel on veut récupérer les cartes
     */
    public void setupCurrentGameMaps(Game game) {
        if (!this.currentGameMaps.isEmpty()) clearGameMaps();

        File dataFolder = new File(Core.get().getPlugin().getDataFolder(), "game_maps/" + game.getMetadata().name());
        if(dataFolder.exists()) {
            for (File file : Objects.requireNonNull(dataFolder.listFiles())) {
                if (file.isFile() && file.getName().endsWith(".schem")) {
                    currentGameMaps.put(file.getName().replace(".schem", ""), MapTypes.SCHEMATIC);
                } else if (file.isDirectory()) {
                    String[] fileList = file.list();
                    if (fileList != null && Arrays.asList(fileList).contains("level.dat")) {
                        currentGameMaps.put(file.getName(), MapTypes.FOLDER);
                    }
                }
            }
        }
    }

    /**
     * Nettoie la Map des cartes du jeu actuel.
     */
    public void clearGameMaps() {
        this.currentGameMaps.clear();
    }

    /**
     * Récupère les cartes disponibles pour un jeu donné
     * @return Liste des cartes disponibles
     */
    public Map<String, MapTypes> getMapsForGame() {
        return currentGameMaps;
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

        switch (this.currentGameMaps.get(mapName)) {
            case SCHEMATIC -> loadSchematicMap(mapName);
            case FOLDER -> loadFolderMap(mapName);
        }
    }

    /**
     * Charge et lance le parsing d'une carte venant d'un schematic
     * @param mapName Nom de la carte
     * @throws MapLoadingException Si le chargement du fichier ou du schematic est impossible
     */
    private void loadSchematicMap(String mapName) throws MapLoadingException {
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
            // Code factorisable ? Peut-être une classe ParseMapArgs qui peut être renvoyé par cette fonction ?
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

    private void loadFolderMap(String mapName) throws MapLoadingException {
        Core.get().getLogger().log(Level.INFO, "Copying world folder {0}...", mapName);

        File sourceFolder = new File(Core.get().getPlugin().getDataFolder(), "game_maps/" + mapName);
        if (!sourceFolder.isDirectory()) throw new MapLoadingException(mapName + " n'est pas un dossier ou n'existe pas.");

        File destination = new File(Bukkit.getPluginsFolder().getParent());
        File newFolder;

         try {
             FileUtils.copyDirectory(sourceFolder, destination);
             newFolder = new File(destination, mapName);
             if (!newFolder.renameTo(new File(destination, WorldUtils.getNormalizedWorldName(mapName)))) throw new IOException("Couldn't rename the folder to " + WorldUtils.getNormalizedWorldName(mapName));
         } catch (IOException e) {
             throw new MapLoadingException("Impossible de copier le dossier dans la racine du serveur : " + e.getMessage());
         }

         org.bukkit.World world = WorldUtils.createWorld(mapName);
         if (Core.get().getGameManager().getCurrentGame() != null) {
            //Todo firstBoundary sera trouvé car il sera le WorldSpawn.
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
