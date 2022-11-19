package fr.jiveoff.efrei.minigames.core.players;

import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.IManager;
import fr.jiveoff.efrei.minigames.core.games.GameManager;

import java.util.HashSet;
import java.util.Set;

/**
 * Gestionnaire des joueurs de jeux.<br /><br />
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class PlayerManager implements IManager {

    private final Set<Player> players;

    /**
     * Constructeur du gestionnaire de joueurs. Il initialise la liste des joueurs aux joueurs connectés actuellement.
     */
    public PlayerManager() {
        if(Core.getInstance().getPlayerManager() != null) {
            throw new IllegalStateException("PlayerManager already initialized !");
        }
        this.players = new HashSet<>();
    }

    @Override
    public void runManager() {
        for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            this.addPlayer(new Player(player));
        }
    }

    /**
     * Ajoute un joueur à la liste des joueurs.
     * @param p Joueur à ajouter
     */
    public void addPlayer(Player p) {
        this.players.add(p);
        Core.getInstance().getTeamManager().dispatchPlayerInTeams(p, false);

        if(Core.getInstance().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            p.spawnAtWaitingLobby();
        }
    }

    /**
     * Supprime un joueur de la liste des joueurs. Déclenche également la vérification pour voir si le jeu doit être arrêté.
     * @param player Joueur à supprimer
     */
    public void removePlayer(Player player) {
        player.unload();
        this.players.remove(player);
    }

    /**
     * Supprime un joueur de la liste des joueurs.
     * @param player Joueur à supprimer
     */
    public void removePlayer(org.bukkit.entity.Player player) {
        this.removePlayer(this.getPlayer(player));
    }

    /**

     * Retourne un joueur à partir de son entité {@link org.bukkit.entity.Player}.
     * @param player Entité du joueur
     * @return Liste des joueurs
     */
    public Player getPlayer(org.bukkit.entity.Player player) {
        for (Player p : this.players) {
            if(p.entity().equals(player)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Retourne tous les joueurs.
     * @return Liste des joueurs
     */
    public Set<Player> getPlayers() {
        return players;
    }

    /**
     * Récupère le nombre de joueurs actuellement en train de jouer
     * @return Nombre de joueurs actuellement en train de jouer
     */
    public Set<Player> getPlayingPlayers() {
        Set<Player> playingPlayers = new HashSet<>();
        if(Core.getInstance().getGameManager().getCurrentGame() == null) {
            playingPlayers.addAll(this.players);
        } else {
            for(Player player : getPlayers()) {
                if(player.getTeam() != null && player.getTeam().isPlayingTeam()) {
                    playingPlayers.add(player);
                }
            }
        }
        return playingPlayers;
    }

    /**
     * Récupère le nombre de joueurs actuellement en train de jouer
     * @return Nombre de joueurs actuellement en train de jouer
     */
    public int getNumberOfPlayingPlayers() {
        return getPlayingPlayers().size();
    }

}
