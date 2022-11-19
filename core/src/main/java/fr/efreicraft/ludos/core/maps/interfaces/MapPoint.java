package fr.efreicraft.ludos.core.maps.interfaces;

import org.bukkit.Location;

/**
 * Définie un point de carte abstrait.
 * Un point est défini par un nom et une {@link Location}.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public abstract class MapPoint {

    /**
     * Nom du point
     */
    protected final String name;

    /**
     * Location du point
     */
    protected final Location location;

    /**
     * Constructeur de MapPoint.
     * @param name Nom du point.
     * @param location Location du point.
     */
    protected MapPoint(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    /**
     * Constructeur de MapPoint sans nom.
     * @param location Location du point.
     */
    protected MapPoint(Location location) {
        this("Unknown", location);
    }

    /**
     * Retourne le nom du point.
     * @return Nom du point.
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne la location du point.
     * @return Location du point.
     */
    public Location getLocation() {
        return location;
    }

}
