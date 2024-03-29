package fr.efreicraft.ludos.core.maps.points;

import fr.efreicraft.ludos.core.maps.interfaces.MapPoint;
import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.Location;

/**
 * Représente un point de spawn. Cette classe est utilisée pour représenter un point de spawn propre à chaque équipe.
 * <br/>
 * Les points de spawn sont représentés par des blocs de laine, de la couleur de la team reliée.
 * <br/>
 * <b>Attention : Les locations sont décalées pour permettre au joueur d'être au centre du bloc.</b>
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class SpawnPoint extends MapPoint {

    private final Team team;

    /**
     * Constructeur de SpawnPoint.
     * @param team Équipe du point.
     * @param location Location du point.
     *                 <b>Attention: Les locations sont décalées pour permettre au joueur d'être au centre du bloc.</b>
     */
    public SpawnPoint(Team team, Location location) {
        super(location);
        this.team = team;
        this.location.add(0.5, 0, 0.5); // Center Location for player spawn
    }

    /**
     * Retourne l'équipe du point.
     * @return Équipe du point.
     */
    public Team getTeam() {
        return team;
    }

}
