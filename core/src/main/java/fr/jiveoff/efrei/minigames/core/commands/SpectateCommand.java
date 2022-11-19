package fr.jiveoff.efrei.minigames.core.commands;

import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.games.GameManager;
import fr.jiveoff.efrei.minigames.core.players.Player;
import fr.jiveoff.efrei.minigames.core.teams.Team;
import fr.jiveoff.efrei.minigames.core.utils.MessageUtils;
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
        Player player = Core.getInstance().getPlayerManager().getPlayer((org.bukkit.entity.Player) sender);
        if(Core.getInstance().getGameManager().getStatus() != GameManager.GameStatus.WAITING) {
            player.sendMessage(MessageUtils.ChatPrefix.TEAM, "&cVous ne pouvez pas activer le mode spectateur en cours de partie.");
            return false;
        }
        if(Core.getInstance().getGameManager().getCurrentGame() == null) {
            player.sendMessage(MessageUtils.ChatPrefix.TEAM, "&cAucun jeu chargé.");
            return false;
        }
        Team specTeam = Core.getInstance().getTeamManager().getTeam("SPECTATORS");
        if(specTeam == null) {
            player.sendMessage(MessageUtils.ChatPrefix.TEAM, "&cMode spectateur indisponible.");
            return false;
        }
        if(player.getTeam() == specTeam) {
            Core.getInstance().getTeamManager().dispatchPlayerInTeams(player, true);
            return true;
        }
        specTeam.addPlayer(player);
        return true;
    }

}
