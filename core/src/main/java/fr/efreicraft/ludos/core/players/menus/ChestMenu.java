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
 * Classe représentant un menu de type <b>chest</b> (inventaire de coffre).<br />
 * Nous ouvrons un inventaire de coffre et nous y insérons les items.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class ChestMenu extends Menu {

    /**
     * L'inventaire du menu.
     */
    private Inventory inventory;

    /**
     * La taille du coffre à ouvrir.
     */
    private int size;

    /**
     * Constructeur de base.
     *
     * @param player   Le joueur concerné par le menu.
     * @param menuName Le titre du menu.
     * @param size     La taille du coffre à ouvrir.
     * @param items    Les items à afficher dans le menu.
     */
    public ChestMenu(Player player, String menuName, int size, List<MenuItem> items) {
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

    /**
     * Schedule une tâche qui va mettre à jour l'inventaire du joueur toutes les secondes (20 ticks).
     */
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
