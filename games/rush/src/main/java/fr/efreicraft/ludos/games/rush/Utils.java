package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;

import javax.annotation.Nullable;

public class Utils {

    @Nullable
    public static fr.efreicraft.ludos.core.players.Player getLudosPlayer(org.bukkit.entity.Player player) {
        return Core.get().getPlayerManager().getPlayer(player);
    }

    public static fr.efreicraft.ludos.core.teams.Team getTeam(String team) {
        return Core.get().getTeamManager().getTeam(team);
    }
}
