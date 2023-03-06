package fr.efreicraft.ludos.core.teams.interfaces;

import fr.efreicraft.ludos.core.players.LudosPlayer;

/**
 * Interface de comportement du respawn des joueurs.
 * @author Aurelien D. {@literal <aurelien.dasse@efrei.net>}
 * @project EFREI-Minigames
 */
public interface ITeamPlayerSpawnCondition {

    /**
     * S'occupe de définir le comportement du respawn des joueurs de l'équipe.
     * @param player Joueur à respawn.
     */
    boolean respawnable (LudosPlayer player);
}
