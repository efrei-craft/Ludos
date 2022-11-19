package fr.efreicraft.ludos.core;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

/**
 * Evenements de Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class EventListener implements Listener {

    /**
     * Evenement de connexion d'un joueur.
     * @param event Evenement Bukkit
     */
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerSpawn(PlayerSpawnLocationEvent event) {
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            event.setSpawnLocation(Core.get().getMapManager().getLobbyWorld().getSpawnLocation().add(-0.5, 0, -0.5));
        } else {
            event.setSpawnLocation(Core.get().getMapManager().getCurrentMap().getMiddleOfMap());
        }
    }

    /**
     * Evenement de spawn d'un joueur sur le serveur.
     * @param event Evenement Bukkit
     */
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.text(MessageUtils.getText(MessageUtils.ChatPrefix.MINIGAMES, "&b" + event.getPlayer().getName() + "&7 a rejoins la partie !")));
        Core.get().getPlayerManager().addPlayer(new Player(event.getPlayer()));
    }

    /**
     * Evenement de deconnexion d'un joueur du serveur.
     * @param event Evenement Bukkit
     */
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.quitMessage(Component.text(MessageUtils.getText(MessageUtils.ChatPrefix.MINIGAMES, "&b" + event.getPlayer().getName() + "&7 a quitté la partie !")));
        Core.get().getPlayerManager().removePlayer(event.getPlayer());
    }

    /**
     * Vérifie si le joueur a changé de Block X & Z, pour laisser le saut disponible.
     * @param event Evenement {@link PlayerMoveEvent} Bukkit.
     * @return Vrai si le joueur a changé de Block X & Z, faux sinon.
     */
    private boolean hasMovedInBlockXAndBlockZ(@NotNull PlayerMoveEvent event) {
        return event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockZ() != event.getTo().getBlockZ();
    }

    /**
     * Evenement de deplacement d'un joueur.
     * @param event Evenement Bukkit
     */
    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(
                hasMovedInBlockXAndBlockZ(event)
                && Core.get().getGameManager().getStatus() == GameManager.GameStatus.STARTING
        ) {
            Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
            if(player.getTeam().isPlayingTeam()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if(event.getEntity() instanceof org.bukkit.entity.Player
                && Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        player.deathEvent(event);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        player.respawnEvent(event);
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        Player player = Core.get().getPlayerManager().getPlayer(event.getPlayer());
        player.postRespawnEvent();
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = org.bukkit.event.EventPriority.HIGHEST)
    public void onPlayerPortalEvent(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

}
