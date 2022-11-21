package fr.efreicraft.ludos.core.games;

import fr.efreicraft.ludos.core.games.interfaces.GameWinner;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe pour les équipes gagnantes.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class TeamWin implements GameWinner {

    private Team team;

    public TeamWin(Team winner) {
        this.team = winner;
    }

    @Override
    public Color getFireworkColor() {
        return team.getColor().bukkitColor();
    }

    @Override
    public List<Player> getPlayers() {
        return new ArrayList<>(team.getPlayers());
    }

    @Override
    public String getWinnerColoredName() {
        return "&fL'équipe " + LegacyComponentSerializer.legacyAmpersand().serialize(
                Component.text(team.getName()).color(team.getColor().textColor())
        );
    }
}
