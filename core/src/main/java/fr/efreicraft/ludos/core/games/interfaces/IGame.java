package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.maps.interfaces.GamePoint;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.Listener;

import java.util.EnumMap;
import java.util.Map;

/**
 * Interface que les jeux doivent implémenter
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public interface IGame {

    /**
     * Méthode appelée avant le parsing de la map.
     * @param world Monde de la map.
     */
    void preMapParse(World world);

    /**
     * Méthode appelée après le parsing de la map.
     */
    void postMapParse();

    /**
     * Méthode appelée pour commencer le jeu.
     */
    void beginGame();

    /**
     * Méthode appelée pour initialiser le scoreboard du joueur pour le jeu actuel.
     * @param player Joueur.
     */
    void setupScoreboard(Player player);

    /**
     * Récupère les matériaux associés aux points pour la construction des
     * {@link GamePoint}.
     * @return Map des matériaux.
     */
    EnumMap<Material, String> getGamePointsMaterials();

    /**
     * Méthode pour récupérer les équipes à créer pour le bon fonctionnement du jeu.
     * @return Map des enregistrements d'équipe.
     */
    Map<String, TeamRecord> getTeamRecords();

    /**
     * @return An event listener instance that will be registered when the game starts (ie. right before players are
     *         spawned in the map), and unloaded when the game ends.
     * @see <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/package-summary.html">Minecraft events
     *      reference</a>
     * @see Game#setEventListener(Listener) setter that may be used to avoid implementing this method
     */
    Listener getEventListener();
}
