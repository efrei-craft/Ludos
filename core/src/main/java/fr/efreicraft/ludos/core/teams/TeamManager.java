package fr.efreicraft.ludos.core.teams;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.players.Player;
import org.bukkit.DyeColor;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Gestionnaire des équipes.<br /><br />
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class TeamManager implements IManager {

    /**
     * Equipes chargées.
     */
    private Map<String, Team> teams;

    /**
     * Constructeur du gestionnaire d'équipes.
     */
    public TeamManager() {
        if(Core.get().getTeamManager() != null) {
            throw new IllegalStateException("TeamManager already initialized");
        }
        this.teams = new HashMap<>();
    }

    @Override
    public void runManager() {
        unregisterAllBukkitTeams();
    }

    /**
     * Charge des équipes.
     * @param teams Map KV des équipes à charger.
     */
    public void loadTeams(Map<String, TeamRecord> teams) {
        unregisterAllBukkitTeams();
        this.teams = new HashMap<>();
        for (Map.Entry<String, TeamRecord> entry : teams.entrySet()) {
            this.teams.put(entry.getKey(), new Team(entry.getValue()));
        }
        for (Team team : this.teams.values()) {
            team.loadTeam();
        }
    }

    private void unregisterAllBukkitTeams() {
        for (org.bukkit.scoreboard.Team team : Core.get().getScoreboardManager().getMainScoreboard().getTeams()) {
            team.unregister();
        }
    }

    /**
     * Décharge les équipes chargées.
     */
    public void unloadTeams() {
        for (Team team : teams.values()) {
            team.unloadTeam();
        }
        this.teams = new HashMap<>();
    }

    /**
     * Retourne une équipe par son clé dans la map.
     * @param name Nom de l'équipe.
     * @return L'équipe.
     */
    public Team getTeam(String name) {
        return this.teams.get(name);
    }

    /**
     * Dispatch un joueur dans soit l'équipe des joueurs ou l'équipe la moins remplie sinon,
     * soit l'équipe des spectateurs si le jeu est en cours.
     * @param player Joueur à dispatch.
     * @param forceRebalance Force le rebalance des équipes.
     */
    public void dispatchPlayerInTeams(Player player, boolean forceRebalance) {
        if(this.teams.size() == 0) {
            return;
        }
        if(player.getTeam() == null || forceRebalance) {
            if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.WAITING) {
                this.getTeam("SPECTATORS").addPlayer(player);
            } else if(this.teams.size() == 2) {
                this.teams.values().iterator().next().addPlayer(player);
            } else {
                Team teamWithLeastPlayers = getTeamWithLeastPlayers();
                if(teamWithLeastPlayers != null) {
                    teamWithLeastPlayers.addPlayer(player);
                }
            }
        }
    }

    /**
     * Dispatch les joueurs dans les équipes de façon équitable.
     */
    public void dispatchAllPlayersInTeams() {
        if(this.teams.size() == 0) {
            return;
        }
        for (Player player : Core.get().getPlayerManager().getPlayers()) {
            dispatchPlayerInTeams(player, false);
        }
    }

    /**
     * Retourne l'équipe avec le moins de joueurs.
     * @return L'équipe avec le moins de joueurs.
     */
    private Team getTeamWithLeastPlayers() {
        /*Iterator<Team> iterator = teams.values().iterator();
        Team team = null;
        while(iterator.hasNext()) {
            Team next = iterator.next();
            if(next.isPlayingTeam() && (team == null || (next.getPlayers().size() < team.getPlayers().size()))) {
                team = next;
            }
        }*/
        return teams.values().stream().min(Comparator.comparingInt(t -> t.getPlayers().size())).get();
    }

    /**
     * Retourne une équipe par sa {@link DyeColor} (pour les points de spawn).
     * @param color La couleur.
     * @return L'équipe.
     */
    public Team getTeamByDyeColor(DyeColor color) {
        for (Team team : this.teams.values()) {
            if (team.getColor().dyeColor() == color) {
                return team;
            }
        }
        return null;
    }

    /**
     * Retourne les équipes chargées.
     * @return Une map KV des équipes chargées.
     */
    public Map<String, Team> getTeams() {
        return this.teams;
    }

}
