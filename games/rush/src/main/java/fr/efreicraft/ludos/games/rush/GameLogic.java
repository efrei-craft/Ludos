package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.entity.Villager;

public class GameLogic {

    private int yDeath;
    final Villager merchant;

    public GameLogic() {
        merchant = null;
    }

    public void yDeath(int yDeath) {
        this.yDeath = yDeath;
    }
    
    //TODO
    /**
     * Vérifie si le lit de *team* a été détruit.
     * @param team L'équipe à vérifier
     * @return {@code true} si le lit de team est détruit, sinon {@code false}.
     */
    public boolean bedDestroyed(Team team) {
        return false;
    }

    /**
     * Déclare le lit de *team* comme étant détruit.
     * @param team L'équipe qui a perdu son lit
     */
    public void bedDestroyed(Team team, boolean destroyed) {

    }

}
