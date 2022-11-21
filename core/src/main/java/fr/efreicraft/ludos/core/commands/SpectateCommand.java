package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class SpectateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = Core.get().getPlayerManager().getPlayer((org.bukkit.entity.Player) sender);
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.WAITING) {
            player.sendMessage(MessageUtils.ChatPrefix.TEAM, "&cVous ne pouvez pas activer le mode spectateur en cours de partie.");
            return false;
        }
        if(Core.get().getGameManager().getCurrentGame() == null) {
            player.sendMessage(MessageUtils.ChatPrefix.TEAM, "&cAucun jeu chargé.");
            return false;
        }
        Team specTeam = Core.get().getTeamManager().getTeam("SPECTATORS");
        if(specTeam == null) {
            player.sendMessage(MessageUtils.ChatPrefix.TEAM, "&cMode spectateur indisponible.");
            return false;
        }
        if(player.getTeam() == specTeam) {
            Core.get().getTeamManager().dispatchPlayerInTeams(player, true);
            return true;
        }
        specTeam.addPlayer(player);
        return true;
    }

}
