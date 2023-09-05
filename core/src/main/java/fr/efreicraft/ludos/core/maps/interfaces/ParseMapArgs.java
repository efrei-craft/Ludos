package fr.efreicraft.ludos.core.maps.interfaces;

import org.bukkit.Location;
import org.bukkit.World;

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
