package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.Core;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Classe utilitaire pour les transmissions serveur de jeu -> proxy.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class GameServerRedisDispatcher {

    private GameServerRedisDispatcher() {
        throw new IllegalStateException("Utility class");
    }

    private static final String SERVO_PROXY_CHANNEL_ENV = "SERVO_PROXY_CHANNEL";

    /**
     * Envoie un message au proxy pour lui signifier que ce serveur est prêt.
     */
    public static void serverReady(Boolean ready) {
        if(Core.get().getRedisClient().isReady()) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
                try {
                    Core.get().getLogger().info("Sending server ready message to proxy...");
                    Core.get().getRedisClient().connectPubSub().sync().publish(
                            System.getenv(SERVO_PROXY_CHANNEL_ENV),
                            InetAddress.getLocalHost().getHostName() + "##READY##" + ready
                    );
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Envoie un message au proxy pour lui transmettre le statut du serveur.
     */
    public static void serverStatus() {
        if(Core.get().getRedisClient().isReady()) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
                try {
                    Core.get().getLogger().info("Sending server status to proxy...");
                    Core.get().getRedisClient().connectPubSub().sync().publish(
                            System.getenv(SERVO_PROXY_CHANNEL_ENV),
                            InetAddress.getLocalHost().getHostName() + "##SET_STATUS##" + Core.get().getGameManager().getStatus().name()
                    );
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Envoie un message au proxy pour lui transmettre le jeu actuel du serveur.
     */
    public static void game() {
        if(Core.get().getRedisClient().isReady()) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
                try {
                    Core.get().getLogger().info("Sending game to proxy: " + Core.get().getGameManager().getCurrentPlugin().getName());
                    Core.get().getRedisClient().connectPubSub().sync().publish(
                            System.getenv(SERVO_PROXY_CHANNEL_ENV),
                            InetAddress.getLocalHost().getHostName() + "##SET_GAME##" + Core.get().getGameManager().getCurrentPlugin().getName()
                    );
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * Envoie le nombre de joueurs dans une team jouant au jeu actuel (non-spec).
     */
    public static void playerCountTimer() {
        if(Core.get().getRedisClient().isReady()) {
            Bukkit.getScheduler().runTaskTimerAsynchronously(Core.get().getPlugin(), () -> {
                try {
                    Core.get().getRedisClient().connectPubSub().sync().publish(
                            System.getenv(SERVO_PROXY_CHANNEL_ENV),
                            InetAddress.getLocalHost().getHostName() + "##SET_PLAYER_COUNT##" + Core.get().getPlayerManager().getNumberOfPlayingPlayers()
                    );
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }, 0, 20L * 3L);
        }
    }

    /**
     * Envoie un message au proxy pour lui dire au revoir.
     */
    public static void bye() {
        if(Core.get().getRedisClient().isReady()) {
            Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
                try {
                    Core.get().getLogger().info("Sending bye to proxy...");
                    Core.get().getRedisClient().connectPubSub().sync().publish(
                            System.getenv(SERVO_PROXY_CHANNEL_ENV),
                            InetAddress.getLocalHost().getHostName() + "##BYE##TRUE"
                    );
                } catch (UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

}
