package fr.efreicraft.ludos.games.blockparty.utils;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

/**
 * Utility methods related to players, used by block party game
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Ludos/BlockParty
 */
public class PlayersUtils {
    public static Set<LudosPlayer> getAllPlayers() {
        return Core.get().getTeamManager().getTeam("PLAYERS").getPlayers();
    }
    public static void clearAllPlayersInventory() {
        for (LudosPlayer player : getAllPlayers()) {
            player.entity().getInventory().clear();
        }
    }
    public static void setItemInMainHandToAll(ItemStack blockToDelete) {
        for (LudosPlayer player : getAllPlayers()) {
            player.entity().getInventory().setItemInMainHand(blockToDelete);
        }
    }
}
