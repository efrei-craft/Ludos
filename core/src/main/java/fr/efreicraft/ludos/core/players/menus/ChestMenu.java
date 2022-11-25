package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import fr.efreicraft.ludos.core.players.menus.interfaces.Menu;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

/**
 * Classe pour l'instanciation d'un menu de type chest.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class ChestMenu extends Menu {

    Inventory inventory;

    int size;

    public ChestMenu(Player player, String menuName, List<MenuItem> items, int size) {
        super(
                player,
                LegacyComponentSerializer.legacyAmpersand().deserialize(menuName),
                items
        );
        this.size = size;
    }

    @Override
    public void show() {
        this.inventory = Bukkit.createInventory(null, this.size, this.menuName);
        this.prepareMenuItems();
        this.player.entity().openInventory(this.inventory);
        scheduleUpdateInventoryTask();
    }

    @Override
    public void close() {
        this.player.entity().closeInventory();
    }

    @Override
    public void prepareMenuItems() {
        inventory.clear();

        for (MenuItem item : this.items) {
            ItemStackMenuItem menuItem = (ItemStackMenuItem) item;
            inventory.setItem(menuItem.getSlot(), menuItem.getItemStack());
        }
    }

    private void scheduleUpdateInventoryTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                InventoryView view = player.entity().getOpenInventory();
                inventory = view.getTopInventory();
                if(!view.title().contains(menuName)) {
                    this.cancel();
                    return;
                }
                prepareMenuItems();
            }
        }.runTaskTimer(Core.get().getPlugin(), 0, 20);
    }


}
