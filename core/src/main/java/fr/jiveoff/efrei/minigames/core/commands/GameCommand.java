package fr.jiveoff.efrei.minigames.core.commands;

import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.games.GameStatusException;
import fr.jiveoff.efrei.minigames.core.players.Player;
import fr.jiveoff.efrei.minigames.core.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Commande /game
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GameCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = Core.getInstance().getPlayerManager().getPlayer((org.bukkit.entity.Player) sender);

        if(args.length == 0) {
            player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /game <list | load | start | stop> <name>");
            return false;
        }

        switch (args[0]) {
            case "list" -> player.sendMessage(
                    MessageUtils.ChatPrefix.GAME,
                    "&7Jeux disponibles: &r" + Core.getInstance().getGameManager().getAvailableGames()
            );
            case "load" -> {
                if (args.length == 1) {
                    player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /game load <name>");
                    return false;
                }
                MessageUtils.broadcast(
                        MessageUtils.ChatPrefix.ADMIN,
                        "&b" + sender.getName() + "&7 a changé le prochain jeu."
                );
                try {
                    Core.getInstance().getGameManager().loadGame(args[1]);
                } catch (GameStatusException e) {
                    player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                    return false;
                }
            }
            case "start" -> {
                MessageUtils.broadcast(
                        MessageUtils.ChatPrefix.ADMIN,
                        "&b" + sender.getName() + "&7 a forcé le démarrage de la partie."
                );
                try {
                    Core.getInstance().getGameManager().startCurrentGame();
                } catch (GameStatusException e) {
                    player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                }
            }
            case "stop" -> {
                MessageUtils.broadcast(
                        MessageUtils.ChatPrefix.ADMIN,
                        "&b" + sender.getName() + "&7 a forcé l'arrêt de la partie."
                );
                try {
                    Core.getInstance().getGameManager().endCurrentGame();
                } catch (GameStatusException e) {
                    player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                }
            }
            default -> player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /game <list | load | start | stop> <name>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return Arrays.asList("list", "load", "start", "stop");
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("load")) {
            return Core.getInstance().getGameManager().getAvailableGames();
        }
        return new ArrayList<>();
    }
}
