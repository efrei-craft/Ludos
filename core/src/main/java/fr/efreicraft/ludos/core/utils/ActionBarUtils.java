package fr.efreicraft.ludos.core.utils;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/**
 * Classe utilitaire pour l'affichage des titres.
 *
 * @author Aurélien D. {@literal <aurelien.dasse@efrei.net>}
 * @project Ludos
 */
public class ActionBarUtils {

    private ActionBarUtils() {
        throw new IllegalStateException("Utility class");
    }


    /**
     * Envoie un action bar à un joueur
     * @param player le joueur qui va recevoir action bar
     * @param message le message envoyé au joueur
     */
    public static void sendActionBar(Player player, String message){
        player.entity().sendActionBar(LegacyComponentSerializer.legacyAmpersand().deserialize(message));
    }

    /**
     * Envoie un action bar à tous les joueurs
     * @param message le message qui va être envoyé
     */
    public static void broadcastActionBar(String message){
        for(Player player : Core.get().getPlayerManager().getPlayers()) {
            sendActionBar(player, message);
        }
    }

}
