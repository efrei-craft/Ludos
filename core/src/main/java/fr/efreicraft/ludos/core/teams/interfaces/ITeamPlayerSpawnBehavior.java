package fr.efreicraft.ludos.core.teams.interfaces;

import fr.efreicraft.ludos.core.players.LudosPlayer;

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
    void spawnPlayer(LudosPlayer player);

}
