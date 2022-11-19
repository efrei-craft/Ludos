package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.players.Player;
import org.bukkit.Color;

import java.util.Collections;
import java.util.List;

/**
 * Classe pour les joueurs gagnants.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class PlayerWin implements GameWinner {
    private Player player;

    public PlayerWin(Player winner) {
        this.player = winner;
    }

    @Override
    public Color getFireworkColor() {
        return org.bukkit.Color.fromRGB(255, 255, 255);
    }

    @Override
    public List<Player> getPlayers() {
        return Collections.singletonList(player);
    }

    @Override
    public String getWinnerColoredName() {
        return player.getName();
    }
}
