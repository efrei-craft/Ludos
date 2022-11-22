package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.menus.ChestMenu;
import fr.efreicraft.ludos.core.players.menus.ChestMenuItem;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player player = Core.get().getPlayerManager().getPlayer((org.bukkit.entity.Player) sender);

        List<MenuItem> items = new ArrayList<>();
        items.add(
                new ChestMenuItem(
                        new ItemStack(Material.CRAFTING_TABLE),
                        6,
                        () -> new ChestMenuItem(
                                new ItemStack(Material.DIAMOND),
                                "&6Test",
                                "&8Test\n\npog!!! &7aaaa &k!!!! &f&l" + System.currentTimeMillis()
                        ),
                        player1 -> player1.sendMessage(MessageUtils.ChatPrefix.GAME, "Test!!")
                )
        );

        player.getMenu().set(
                new ChestMenu(
                        player,
                        "&8» &6Test",
                        items,
                        9
                )
        );
        player.getMenu().get().open();

        return true;
    }
}
