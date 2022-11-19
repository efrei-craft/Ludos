package fr.jiveoff.efrei.minigames.core.teams;

import com.google.common.collect.ImmutableMap;
import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.players.Player;
import fr.jiveoff.efrei.minigames.core.utils.ColorUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;

import java.util.Map;

/**
 * Classe générant les équipes par défaut.
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class DefaultTeamRecordBuilder {

    public enum DefaultTeamRecords {
        /**
         * Equipe Spectateurs pour les jeux spécifiant des teams customisées.
         */
        ONLY_SPECTATOR(ImmutableMap.<String, TeamRecord>builder()
                .put("SPECTATORS", new TeamRecord(
                        "Spectateurs",
                        1,
                        false,
                        false,
                        new ColorUtils.TeamColorSet(NamedTextColor.DARK_GRAY, DyeColor.GRAY, Color.GRAY),
                        player -> {
                            player.entity().setGameMode(org.bukkit.GameMode.SPECTATOR);
                            for (Player p : Core.getInstance().getPlayerManager().getPlayingPlayers()) {
                                p.entity().hidePlayer(Core.getInstance().getPlugin(), player.entity());
                            }
                        }
                ))
                .build()
        ),

        /**
         * Si un jeu est en solo, il faut utiliser cette équipe si le jeu ne spécialise pas les joueurs.<br />
         * L'équipe PLAYERS et SPECTATORS sont utilisées ici.
         */
        DEFAULT_TEAMS_SOLO(ImmutableMap.<String, TeamRecord>builder()
                .put("PLAYERS", new TeamRecord(
                        "Joueurs",
                        1,
                        false,
                        true,
                        new ColorUtils.TeamColorSet(NamedTextColor.GRAY, DyeColor.WHITE, Color.WHITE),
                        null
                ))
                .putAll(ONLY_SPECTATOR.getTeamRecords())
                .build()
        ),
        ;

        private final Map<String, TeamRecord> teams;

        /**
         * Constructeur de l'énumération.
         * @param teams Map KV des équipes.
         */
        DefaultTeamRecords(ImmutableMap<String, TeamRecord> teams) {
            this.teams = teams;
        }

        /**
         * Retourne la liste des équipes du set par défaut choisi.
         * @return Liste des équipes du set par défaut choisi.
         */
        public Map<String, TeamRecord> getTeamRecords() {
            return teams;
        }
    }

    /**
     * Méthode permettant de copier un team record pour modifier son comportement de spawn joueur.
     * @param teamRecord Team record à copier.
     * @param behavior Nouveau comportement de spawn joueur.
     * @return Nouveau team record avec le comportement de spawn joueur modifié.
     */
    public static TeamRecord copyTeamRecordWithCustomBehavior(TeamRecord teamRecord, ITeamPlayerSpawnBehavior behavior) {
        return new TeamRecord(
                teamRecord.name(),
                teamRecord.priority(),
                teamRecord.showTeamName(),
                teamRecord.playingTeam(),
                teamRecord.colorSet(),
                behavior
        );
    }

}
