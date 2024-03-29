package fr.efreicraft.ludos.core.players;

import fr.efreicraft.ecatup.players.ECPlayer;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.utils.MessageUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Gestionnaire des joueurs de jeux.<br /><br />
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class PlayerManager implements IManager {

    private final Set<LudosPlayer> players;

    /**
     * Constructeur du gestionnaire de joueurs. Il initialise la liste des joueurs aux joueurs connectés actuellement.
     */
    public PlayerManager() {
        if(Core.get().getPlayerManager() != null) {
            throw new IllegalStateException("PlayerManager already initialized !");
        }
        this.players = new HashSet<>();
    }

    @Override
    public void runManager() {
        /* for (org.bukkit.entity.Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            this.addPlayer(new Player(player));
        }*/
    }

    /**
     * Ajoute un joueur à la liste des joueurs.
     * @param p Joueur à ajouter
     */
    public void addPlayer(LudosPlayer p) {
        this.players.add(p);
        Core.get().getTeamManager().dispatchPlayerInTeams(p, false);

        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            p.spawnAtWaitingLobby();
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.SERVER, "&b" + p.getName() + " &7a &arejoint&7 la partie.");
        }
    }

    /**
     * Supprime un joueur de la liste des joueurs. Déclenche également la vérification pour voir si le jeu doit être arrêté.
     * @param player Joueur à supprimer
     */
    public void removePlayer(LudosPlayer player) {
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.INGAME
                && player.getTeam() != null
                && player.getTeam().isPlayingTeam()) {
            player.entity().setHealth(0);
        }
        player.unload();
        this.players.remove(player);
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.SERVER, "&b" + player.getName() + " &7a &cquitté&7 la partie.");
        }
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
    public LudosPlayer getPlayer(org.bukkit.entity.Player player) {
        for (LudosPlayer p : this.players) {
            if(p.entity().equals(player)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Retourne un joueur à partir de son entité {@link fr.efreicraft.ecatup.players.ECPlayer}.
     * @param ecPlayer Entité du joueur
     * @return Liste des joueurs
     */
    public LudosPlayer getPlayer(ECPlayer ecPlayer) {
        for (LudosPlayer p : this.players) {
            if(p.getEcPlayer().equals(ecPlayer)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Retourne tous les joueurs.
     * @return Liste des joueurs
     */
    public Set<LudosPlayer> getPlayers() {
        return players;
    }

    /**
     * Récupère le nombre de joueurs actuellement en train de jouer
     * @return Nombre de joueurs actuellement en train de jouer
     */
    public Set<LudosPlayer> getPlayingPlayers() {
        Set<LudosPlayer> playingPlayers = new HashSet<>();
        if(Core.get().getGameManager().getCurrentGame() == null) {
            playingPlayers.addAll(this.players);
        } else {
            for(LudosPlayer player : getPlayers()) {
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
