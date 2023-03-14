package fr.efreicraft.ludos.core.games;

import fr.efreicraft.animus.endpoints.ServerService;
import fr.efreicraft.animus.invoker.ApiException;
import fr.efreicraft.ludos.core.Core;
import org.bukkit.Bukkit;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Classe utilitaire pour les transmissions serveur de jeu -> proxy.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class GameServerDispatcher {

    private GameServerDispatcher() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Mets à jour le statut du serveur sur l'API.
     */
    public static void updateStatus() {
        Bukkit.getScheduler().runTaskAsynchronously(Core.get().getPlugin(), () -> {
            try {
                String gameName = Core.get().getGameManager().getCurrentGame() == null
                        ? null
                        : Core.get().getGameManager().getCurrentPlugin().getName();
                ServerService.setGameServerStatus(
                        InetAddress.getLocalHost().getHostName(),
                        gameName,
                        Core.get().getGameManager().getDefaultGamePluginName(),
                        Core.get().getGameManager().getStatus().name()
                );
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
