package fr.efreicraft.ludos.core.maps.points;

import fr.efreicraft.ludos.core.maps.interfaces.MapPoint;
import org.bukkit.Location;

/**
 * Représente un point du jeu. Cette classe est utilisée pour représenter un point propre à chaque jeu.
 * Exemple du BlockParty: DANCE_FLOOR, KILL_ZONE...
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GamePoint extends MapPoint {

    /**
     * Constructeur de GamePoint.
     * @param name Nom du point.
     * @param location Location du point.
     */
    public GamePoint(String name, Location location) {
        super(name, location);
    }

}
