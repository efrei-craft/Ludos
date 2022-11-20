package fr.efreicraft.ludos.core.teams;

import fr.efreicraft.ludos.core.players.Player;

/**
 * Interface de comportement de spawn des joueurs.
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public interface ITeamPlayerSpawnBehavior {

    /**
     * S'occupe de définir le comportement de spawn des joueurs de l'équipe.
     * @param player Joueur à spawn.
     */
    void spawnPlayer(Player player);

}