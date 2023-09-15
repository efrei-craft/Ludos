package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.interfaces.GameWinner;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Utilisé pour définir une certaine équipe comme gagnante du ctf
 * @author Ewenn BAUDET
 * @param teamKey clé associée à la team dans le TeamManager de ludos ("RED" / "BLUE")
 */
public record CtfWinners(String teamKey) implements GameWinner {
    @Override
    public Color getFireworkColor() {
        return Core.get().getTeamManager().getTeam(teamKey).getColor().bukkitColor();
    }

    @Override
    public List<LudosPlayer> getPlayers() {
        Set<LudosPlayer> teamPlayers = Core.get().getTeamManager().getTeam(teamKey).getPlayers();
        return new ArrayList<>(teamPlayers);
    }

    @Override
    public String getWinnerColoredName() {
        String colorCode;
        switch(teamKey) {
            case "RED" -> colorCode = "&c";
            case "BLUE" -> colorCode = "&9";
            default -> colorCode = "";
        }
        return "L'équipe " + colorCode + Core.get().getTeamManager().getTeam(teamKey).getName().toLowerCase();
    }
}
