package fr.efreicraft.ludos.core.teams;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.players.LobbyPlayerHelper;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.interfaces.ITeamPlayerSpawnCondition;
import fr.efreicraft.ludos.core.teams.interfaces.ITeamPlayerSpawnBehavior;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Equipe de mini-jeux.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class Team {

    /**
     * Nom de l'équipe.
     */
    private final String name;

    /**
     * Ajoute ou non le nom de l'équipe au préfixe de l'équipe.
     */
    private final boolean showTeamName;

    /**
     * Priorité de l'équipe dans le tablist.
     */
    private final Integer priority;

    /**
     * Equipe jouant ou non.
     */
    private final boolean playingTeam;

    /**
     * Set de couleurs de l'équipe.
     * @see ColorUtils
     */
    private final ColorUtils.TeamColorSet colorSet;

    /**
     * Liste des joueurs de l'équipe.
     */
    private final Set<Player> players;

    /**
     * Equipe Bukkit de l'équipe.
     */
    private org.bukkit.scoreboard.Team bukkitTeam;

    /**
     * Comportement de spawn des joueurs de l'équipe en jeu.
     */
    private final ITeamPlayerSpawnBehavior spawnBehavior;

    /**
     * Vérifie si les joueurs peuvent respawn
     */
    private final ITeamPlayerSpawnCondition spawnCondition;

    /**
     * Indice du spawn point à utiliser pour le prochain spawn de joueur.
     */
    private int spawnIndex = 0;

    /**
     * Constructeur de l'équipe.
     * @param teamRecord Record de l'équipe.
     */
    public Team(TeamRecord teamRecord) {
        this.name = teamRecord.name();
        this.priority = teamRecord.priority();
        this.showTeamName = teamRecord.showTeamName();
        this.playingTeam = teamRecord.playingTeam();
        this.colorSet = teamRecord.colorSet();
        this.spawnBehavior = teamRecord.spawnBehavior();
        this.spawnCondition = teamRecord.spawnCondition();
        this.players = new HashSet<>();
    }

    /**
     * Génère le nom de l'équipe avec la priorité pour Bukkit.
     * @return Nom de l'équipe avec la priorité.
     */
    private String generateTeamNameWithPriority() {
        return (char) (priority + 64) + "-" + this.name;
    }

    /**
     * Charge l'équipe dans le scoreboard de Bukkit.
     */
    public void loadTeam() {
        this.bukkitTeam = Core.get().getScoreboardManager().getMainScoreboard().registerNewTeam(
                this.generateTeamNameWithPriority()
        );
        if(showTeamName) {
            bukkitTeam.prefix(Component.text()
                    .append(this.name())
                    .append(Component.text(" "))
                    .build());
        }
        bukkitTeam.color(NamedTextColor.nearestTo(this.colorSet.textColor()));
        bukkitTeam.setAllowFriendlyFire(false);
    }

    /**
     * Décharge l'équipe du scoreboard de Bukkit.
     */
    public void unloadTeam() {
        this.removePlayers(players);
        this.bukkitTeam.unregister();
    }

    /**
     * Renvoie le nom de l'équipe.
     * @return {@link net.kyori.adventure.text.TextComponent} du nom de l'équipe.
     */
    public Component name() {
        return Component.text(this.name)
                .color(this.colorSet.textColor())
                .decoration(TextDecoration.BOLD, true);
    }

    /**
     * Renvoie le nom de l'équipe.
     * @return Nom de l'équipe.
     */
    public String getName() {
        return name;
    }

    /**
     * Renvoie le set de couleurs de l'équipe.
     * @return Set de couleurs de l'équipe.
     * @see ColorUtils
     */
    public ColorUtils.TeamColorSet getColor() {
        return colorSet;
    }

    /**
     * Ajoute un joueur à l'équipe.
     * @param player Joueur à ajouter.
     */
    public void addPlayer(Player player) {
        if(this.players.contains(player)) {
            return;
        }
        if(player.getTeam() != null) {
            player.getTeam().removePlayer(player);
        }
        this.players.add(player);
        player.setTeam(this);
        this.bukkitTeam.addEntry(player.entity().getName());
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            LobbyPlayerHelper.preparePlayerItems(player);
        } else {
            this.spawnBehavior.spawnPlayer(player);
        }
    }

    /**
     * Supprime un joueur de l'équipe.
     * @param player Joueur à supprimer.
     */
    public void removePlayer(Player player) {
        if(!this.players.contains(player)) {
            return;
        }
        this.players.remove(player);
        player.clearTeam();
        if(player.entity() != null) {
            this.bukkitTeam.removeEntry(player.entity().getName());
        }
        if(Core.get().getGameManager().getCurrentGame() != null) {
            Core.get().getGameManager().getCurrentGame().checkIfGameHasToBeEnded();
        }
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            LobbyPlayerHelper.preparePlayerItems(player);
        }
    }

    /**
     * Supprime des joueurs de l'équipe.
     * @param players Joueurs à supprimer.
     */
    public void removePlayers(Set<Player> players) {
        for (Player player : players) {
            player.clearTeam();
            if(player.entity() != null) {
                this.bukkitTeam.removeEntry(player.entity().getName());
            }
            if(player.isEphemeral() && !player.entity().hasPermission("ludos.admin")) {
                player.entity().kick(Component.text("Fin de la partie."));
            }
            if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
                LobbyPlayerHelper.preparePlayerItems(player);
            }
        }
    }

    /**
     * Renvoie les points de spawn de l'équipe sur la carte actuellement chargée.
     * @return Liste des points de spawn de l'équipe.
     */
    public List<SpawnPoint> getSpawnPointsForCurrentMap() {
        return Core.get().getMapManager().getCurrentMap().getSpawnPoints().get(this);
    }

    /**
     * Téléporte le joueur au prochain point de spawn de l'équipe.
     * @param player Joueur à téléporter.
     */
    public void teleportPlayerToNextSpawnPoint(Player player) {
        List<SpawnPoint> spawnPoints = this.getSpawnPointsForCurrentMap();
        if(spawnPoints == null) {
            return;
        }
        if(spawnPoints.isEmpty()) {
            return;
        }
        if(spawnIndex >= spawnPoints.size()) {
            spawnIndex = 0;
        }
        Location spawnPoint = spawnPoints.get(spawnIndex).getLocation();
        setYawOfLocation(spawnPoint, Core.get().getMapManager().getCurrentMap().getMiddleOfMap());
        spawnIndex++;

        player.entity().teleport(spawnPoint);
    }

    private void setYawOfLocation(Location playerPos, Location locationToLookAt) {
        double dx = locationToLookAt.getX() - playerPos.getX();
        double dz = locationToLookAt.getZ() - playerPos.getZ();
        double yaw = (Math.atan2(dz, dx) * 180 / Math.PI) - 90;
        playerPos.setYaw((float) yaw);
    }

    /**
     * Exécute le comportement de setup des joueurs de l'équipe.
     * @param player Joueur à setup.
     */
    public void spawnPlayer(Player player) {
        player.resetPlayer();
        if(this.playingTeam) {
            this.teleportPlayerToNextSpawnPoint(player);
        }
        this.spawnBehavior.spawnPlayer(player);
    }

    /**
     * Renvoie les joueurs de l'équipe.
     * @return Liste des joueurs de l'équipe.
     */
    public Set<Player> getPlayers() {
        return players;
    }

    /**
     * Renvoie si l'équipe joue au jeu ou non.
     * @return Si l'équipe joue au jeu ou non.
     */
    public boolean isPlayingTeam() {
        return playingTeam;
    }

    /**
     * Change l'état de friendly fire de l'équipe.
     * @param friendlyFire Nouvel état de friendly fire.
     */
    public void setFriendlyFire(boolean friendlyFire) {
        this.bukkitTeam.setAllowFriendlyFire(friendlyFire);
    }

    /**
     * Renvoie si l'equipe peut respawn ou non.
     * @return Si l'equipe peut respawn ou non.
     */
    public boolean getSpawnCondition(Player player) {
        return this.spawnCondition.respawnable(player);
    }
}
