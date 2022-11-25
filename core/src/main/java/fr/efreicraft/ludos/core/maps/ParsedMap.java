package fr.efreicraft.ludos.core.maps;

import fr.efreicraft.ludos.core.maps.interfaces.MapPoint;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.maps.points.GlobalPoint;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Carte parsée. Cette classe est utilisée pour représenter une carte parsée.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class ParsedMap {

    /**
     * {@link World} de la carte
     */
    private final World world;

    /**
     * Nom de la carte
     */
    private String name = "Unknown";

    /**
     * Auteur de la carte
     */
    private String author = "Unknown";

    /**
     * Milieu de la carte
     */
    private Location middleOfMap = null;

    /**
     * Map des {@link GamePoint} de la carte, mappé en KV sur (nom, point)
     */
    private final HashMap<String, ArrayList<GamePoint>> gamePoints;

    /**
     * Map des {@link GlobalPoint} de la carte, mappé en KV sur (nom, point)
     */
    private final HashMap<String, ArrayList<GlobalPoint>> globalPoints;

    /**
     * Map des {@link SpawnPoint} de la carte, mappé en KV sur (équipe, point)
     */
    private final HashMap<Team, ArrayList<SpawnPoint>> spawnPoints;

    /**
     * Booléen pour savoir si la carte est valide
     */
    private boolean parsed = false;

    /**
     * Constructeur d'une carte parsée.
     * @param world {@link World} de la carte.
     */
    public ParsedMap(World world) {
        this.gamePoints = new HashMap<>();
        this.globalPoints = new HashMap<>();
        this.spawnPoints = new HashMap<>();
        this.world = world;
    }

    /**
     * Ajoute un {@link MapPoint} à la carte selon son type.
     * @param point Point à ajouter
     */
    public void addPoint(MapPoint point) {
        // See https://rules.sonarsource.com/java/RSPEC-6201
        if (point instanceof GamePoint gamePoint) {
            if (!gamePoints.containsKey(gamePoint.getName())) {
                gamePoints.put(gamePoint.getName(), new ArrayList<>());
            }
            gamePoints.get(gamePoint.getName()).add(gamePoint);
        } else if (point instanceof GlobalPoint globalPoint) {
            if (!globalPoints.containsKey(globalPoint.getName())) {
                globalPoints.put(globalPoint.getName(), new ArrayList<>());
            }
            globalPoints.get(globalPoint.getName()).add(globalPoint);
        } else if (point instanceof SpawnPoint spawnPoint) {
            if (!spawnPoints.containsKey(spawnPoint.getTeam())) {
                spawnPoints.put(spawnPoint.getTeam(), new ArrayList<>());
            }
            spawnPoints.get(spawnPoint.getTeam()).add(spawnPoint);
        }
    }

    /**
     * Change le nom de la carte.
     * @param name Nouveau nom de la carte.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Change l'auteur de la carte.
     * @param author Nouvel auteur de la carte.
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Change le booléen de validité de la carte.
     * @param parsed Nouveau booléen de validité de la carte.
     */
    public void setParsed(boolean parsed) {
        this.parsed = parsed;
    }

    /**
     * Récupère le booléen de validité de la carte.
     * @return Booléen de validité de la carte.
     */
    public boolean isParsed() {
        return parsed;
    }

    /**
     * Retourne le nom de la carte.
     * @return Nom de la carte.
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne l'auteur de la carte.
     * @return Auteur de la carte.
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Retourne le {@link World} de la carte.
     * @return {@link World} de la carte.
     */
    public World getWorld() {
        return world;
    }

    /**
     * Retourne les game points de la carte.
     * @return Map de liste de {@link GamePoint} de la carte, mappé en KV sur (nom, points)
     */
    public Map<String, ArrayList<GamePoint>> getGamePoints() {
        return gamePoints;
    }

    /**
     * Retourne les global points de la carte.
     * @return Map de liste de {@link GlobalPoint} de la carte, mappé en KV sur (nom, points)
     */
    public Map<String, ArrayList<GlobalPoint>> getGlobalPoints() {
        return globalPoints;
    }

    /**
     * Retourne les spawn points de la carte.
     * @return Map de liste de {@link SpawnPoint} de la carte, mappé en KV sur (équipe, points)
     */
    public Map<Team, ArrayList<SpawnPoint>> getSpawnPoints() {
        return spawnPoints;
    }

    /**
     * Change la point du milieu de la carte.
     * @param middle Nouveau point de milieu de carte
     */
    public void setMiddleOfMap(Location middle) {
        this.middleOfMap = null; // L'intérêt est que getMiddleOfMap() renverra bien un recalcul si on nullifie this.middleOfMap
        this.middleOfMap = middle != null ? middle : getMiddleOfMap();
    }

    /**
     * Calcule la location du milieu de la carte.
     * @return {@link Location} du milieu de la carte.
     */
    public Location getMiddleOfMap() {
        if (middleOfMap != null) return middleOfMap;

        if (globalPoints.containsKey("MIDDLE"))
            return globalPoints.get("MIDDLE").get(0).getLocation();

        Location first = globalPoints.get("BOUNDARY").get(0).getLocation();
        Location second = globalPoints.get("BOUNDARY").get(1).getLocation();

        double x = (first.getX() + second.getX()) / 2;
        double y = (first.getY() + second.getY()) / 2;
        double z = (first.getZ() + second.getZ()) / 2;

        return new Location(world, x, y, z);
    }

    /**
     * Méthode pour récupérer le point Y le plus bas de la carte.
     * @return Point Y le plus bas de la carte.
     */
    public Location getLowestBoundary() {
        Location first = globalPoints.get("BOUNDARY").get(0).getLocation();
        Location second = globalPoints.get("BOUNDARY").get(1).getLocation();

        if (first.getY() < second.getY()) {
            return first;
        } else {
            return second;
        }
    }

}
