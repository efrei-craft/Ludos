package fr.efreicraft.ludos.core.teams;

import fr.efreicraft.ludos.core.teams.interfaces.ITeamPlayerSpawnBehavior;
import fr.efreicraft.ludos.core.teams.interfaces.ITeamPlayerSpawnCondition;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import org.bukkit.GameMode;

/**
 * Classe record pour définir les équipes lors de la création des équipes par défaut notamment.
 *
 * @param name Nom de l'équipe.
 * @param showTeamName Ajoute ou non le nom de l'équipe au préfixe de l'équipe.
 * @param priority Priorité de l'équipe dans le tablist.
 * @param playingTeam Equipe jouant ou non (spectateurs entre autres).
 * @param colorSet Set de couleurs de l'équipe (voir {@link ColorUtils}).
 * @param spawnBehavior Lambda de spawn des joueurs de l'équipe en jeu, si elle est nulle alors un comportement
 *                      par défaut est utilisé.
 * @param spawnCondition Lambda de respawn des joueurs de l'équipe en jeu, si elle est nulle alors les joueurs
 *                       de l'équipe pourront respawn.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public record TeamRecord(
        String name,
        Integer priority,
        boolean showTeamName,
        boolean playingTeam,
        ColorUtils.TeamColorSet colorSet,
        ITeamPlayerSpawnCondition spawnCondition,
        ITeamPlayerSpawnBehavior spawnBehavior
) {

    public TeamRecord {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Team name cannot be null or empty.");
        }
        if (priority == null) {
            throw new IllegalArgumentException("Team priority cannot be null.");
        }
        if (colorSet == null) {
            throw new IllegalArgumentException("Team color set cannot be null.");
        }
        if (spawnBehavior == null) {
            spawnBehavior = p -> {
                p.entity().setHealth(20);
                p.entity().setFoodLevel(20);
                p.entity().setSaturation(20);
                p.entity().setExp(0);
                p.entity().setLevel(0);
                p.entity().getInventory().clear();
                p.entity().getInventory().setArmorContents(null);
                p.entity().setGameMode(GameMode.ADVENTURE);
            };
        }
        if(spawnCondition == null){
            spawnCondition = p -> true;
        }
    }

}
