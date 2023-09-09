package fr.efreicraft.ludos.core.maps.interfaces;

import fr.efreicraft.ludos.core.maps.MapManager;
import fr.efreicraft.ludos.core.maps.MapParser;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Arguments pour le parsing d'une carte.
 * Son but est de factoriser le code à la fin de {@link MapManager#loadMap}
 *
 * @see MapParser#parseMap
 * @see MapManager#loadMap
 */
public class ParseMapArgs {
    public Location firstBoundary;
    public Location lastBoundary;
    public World world;

    public ParseMapArgs(Location firstBoundary, Location lastBoundary, World world) {
        this.firstBoundary = firstBoundary;
        this.lastBoundary = lastBoundary;
        this.world = world;
    }
}
