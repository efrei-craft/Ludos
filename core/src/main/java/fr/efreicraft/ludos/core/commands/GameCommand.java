package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.exceptions.GameRegisteringException;
import fr.efreicraft.ludos.core.games.exceptions.GameStatusException;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Commande /game
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class GameCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if(args.length == 0) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /game <list | load | start | stop | reset | reload> <name>");
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "list" -> MessageUtils.sendMessage(sender, 
                    MessageUtils.ChatPrefix.GAME,
                    "&7Jeux disponibles: &r" + Core.get().getGameManager().getAvailableGames()
            );
            case "load" -> {
                if (args.length == 1) {
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /game load <name>");
                    return false;
                }
                if(Core.get().getGameManager().getCurrentGame() != null) {
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cUn jeu est déjà chargé. &7Déchargez le avec &e/game reset&7.");
                    return false;
                }
                MessageUtils.broadcastMessage(
                        MessageUtils.ChatPrefix.ADMIN,
                        "&b" + sender.getName() + "&7 a changé le prochain jeu."
                );
                try {
                    if (!args[1].matches("^[A-Z][A-Za-z0-9]*$"))
                        args[1] = args[1].substring(0, 1).toUpperCase() + args[1].substring(1);
                    if (!args[1].startsWith("Ludos"))
                        args[1] = "Ludos" + args[1];
                    Core.get().getGameManager().loadGame(args[1]);
                } catch (GameStatusException e) {
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                    return false;
                } catch (GameRegisteringException e) {
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }
            case "start" -> {
                try {
                    Core.get().getGameManager().startCurrentGame();
                    MessageUtils.broadcastMessage(
                            MessageUtils.ChatPrefix.ADMIN,
                            "&b" + sender.getName() + "&7 a forcé le démarrage de la partie."
                    );
                } catch (GameStatusException e) {
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                }
            }
            case "auto" -> {
                if(Core.get().getGameManager().isAutoGameStart()) {
                    Core.get().getGameManager().setAutoGameStart(false);
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&7Le démarrage automatique de la partie a été &cdésactivé&7.");
                } else {
                    Core.get().getGameManager().setAutoGameStart(true);
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&7Le démarrage automatique de la partie a été &aactivé&7.");
                }
            }
            case "stop" -> {
                try {
                    Core.get().getGameManager().endCurrentGame();
                    MessageUtils.broadcastMessage(
                            MessageUtils.ChatPrefix.ADMIN,
                            "&b" + sender.getName() + "&7 a forcé l'arrêt de la partie."
                    );
                } catch (GameStatusException e) {
                    MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&c" + e.getMessage());
                }
            }
            case "reset" -> {
                Core.get().getGameManager().resetServer();
                MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&7La carte et le jeu ont bien été &anettoyés&7.");
            }
            case "reload" -> {
                Core.get().getGameManager().unloadAllGameJars();
                Core.get().getGameManager().loadAllGameJars();
                MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&7Jeux &arechargés&7.");
            }
            default -> MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /game <list | load | start | stop | reset | reload> <name>");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if(args.length == 1) {
            return Stream.of("list", "load", "start", "stop", "reset", "reload", "auto").filter(com -> com.startsWith(args[0].toLowerCase())).toList();
        }
        if(args.length == 2 && args[0].equalsIgnoreCase("load")) {
            return Core.get().getGameManager().getAvailableGames().stream().filter(game -> game.startsWith(args[1].toLowerCase()) || game.startsWith(args[1].substring(0, 1).toUpperCase() + args[1].substring(1), 5)).toList();
        }
        return new ArrayList<>();
    }
}
