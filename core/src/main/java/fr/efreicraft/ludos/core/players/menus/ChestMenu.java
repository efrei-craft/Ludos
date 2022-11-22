package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.menus.interfaces.Menu;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
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
    public void open() {
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
            ChestMenuItem menuItem = (ChestMenuItem) item;
            ItemStack itemStack = menuItem.getItemStack();

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(menuItem.getName());
            itemMeta.lore(menuItem.getDescription());
            itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(menuItem.getSlot(), itemStack);
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

    /**
     * Récupère l'item par rapport à son slot dans la liste de {@link MenuItem} de l'instance.
     * @param slot Slot de l'item.
     * @return L'item.
     */
    public MenuItem getMenuItem(Integer slot) {
        for (MenuItem item : this.items) {
            ChestMenuItem menuItem = (ChestMenuItem) item;
            if(menuItem.getSlot().equals(slot)) {
                return menuItem;
            }
        }
        return null;
    }


}
