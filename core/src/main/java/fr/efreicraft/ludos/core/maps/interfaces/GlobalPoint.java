package fr.efreicraft.ludos.core.maps.interfaces;

import org.bukkit.Location;

/**
 * Représente un point global. Cette classe est utilisée pour représenter un point propre à tous les jeux.
 * Exemple: BOUNDARY...
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GlobalPoint extends MapPoint {

    /**
     * Constructeur de GlobalPoint.
     * @param name Nom du point.
     * @param location Location du point.
     */
    public GlobalPoint(String name, Location location) {
        super(name, location);
    }

}
