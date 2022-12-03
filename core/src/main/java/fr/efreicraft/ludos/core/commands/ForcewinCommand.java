package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.games.PlayerWin;
import fr.efreicraft.ludos.core.games.TeamWin;
import fr.efreicraft.ludos.core.games.interfaces.GameWinner;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class ForcewinCommand implements CommandExecutor, TabCompleter {

    private List<GameWinner> getPotentialWinners() {
        List<GameWinner> winners = new ArrayList<>();

        for(Player player : Core.get().getPlayerManager().getPlayingPlayers()) {
            PlayerWin win = new PlayerWin(player);
            winners.add(win);
        }

        for(Map.Entry<String, Team> entry : Core.get().getTeamManager().getTeams().entrySet()) {
            TeamWin win = new TeamWin(entry.getValue());
            winners.add(win);
        }

        return winners;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(Core.get().getGameManager().getCurrentGame() == null) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cAucun jeu n'est en cours.");
            return false;
        }

        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cLe jeu n'est pas en cours.");
            return false;
        }

        if(args.length == 0) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /forcewin <joueur | team>");
            return false;
        }

        List<GameWinner> winners = getPotentialWinners();
        GameWinner winner = null;

        for(GameWinner win : winners) {
            if((win instanceof PlayerWin playerWin && playerWin.getPlayer().getName().equalsIgnoreCase(args[0]))
                            || (win instanceof TeamWin teamWin && teamWin.getTeam().getName().equalsIgnoreCase(args[0]))) {
                winner = win;
                break;
            }
        }

        Core.get().getGameManager().getCurrentGame().setWinnerAndEndGame(winner);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = getPotentialWinners().stream().map(gameWinner -> {
            if(gameWinner instanceof PlayerWin playerWin) {
                return playerWin.getPlayer().entity().getName();
            } else if(gameWinner instanceof TeamWin teamWin) {
                return teamWin.getTeam().getName();
            } else {
                return null;
            }
        }).toList();

        if(args.length == 1) {
            return options.stream().filter(option -> option.toLowerCase().startsWith(args[0].toLowerCase())).toList();
        } else {
            return List.of();
        }
    }
}