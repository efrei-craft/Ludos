package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MoveCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<Entity> playerList;
        if (args.length < 1) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.TEAM, "&cSyntaxe: /move <player> <team>");
            return false;
        } else if (args.length == 1) {
            playerList = Bukkit.selectEntities(sender, args[0]);
            if (playerList.isEmpty()) {
                MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.TEAM, "&cPrécisez un joueur connecté !");
                return false;
            }
            for (Entity entity : playerList) {
                if (!(entity instanceof Player player)) continue;

                fr.efreicraft.ludos.core.players.Player lPlayer = Core.get().getPlayerManager().getPlayer(player);
                if (lPlayer != null)
                    lPlayer.clearTeam();
            }

            return false;
        }

        playerList = Bukkit.selectEntities(sender, args[0]);
        if (playerList.isEmpty()) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.TEAM, "&cPrécisez un joueur connecté !");
            return false;
        }
        Team targetTeam = Core.get().getTeamManager().getTeam(args[1]);

        if (targetTeam == null) {
            MessageUtils.sendMessage(sender, MessageUtils.ChatPrefix.TEAM, "&cCette équipe n'existe pas !");
            return false;
        }

        for (Entity entity : playerList) {
            if (!(entity instanceof Player player)) continue;

            fr.efreicraft.ludos.core.players.Player lPlayer = Core.get().getPlayerManager().getPlayer(player);
            targetTeam.addPlayer(lPlayer);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                options.add(player.getName());
            }
        } else if (args.length == 2) {
            for (Map.Entry<String, Team> entry : Core.get().getTeamManager().getTeams().entrySet()) {
                options.add(entry.getKey());
            }
        }
        return options;
    }
}
