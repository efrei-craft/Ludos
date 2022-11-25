package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.menus.interfaces.Menu;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Classe pour l'instanciation d'un menu de type inventaire de joueur.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class PlayerInventoryMenu extends Menu {

    public PlayerInventoryMenu(Player player, List<MenuItem> items) {
        super(player, null, items);
    }

    @Override
    public void show() {
        this.prepareMenuItems();
    }

    @Override
    public void close() {
        // Fermer l'inventaire du joueur est impossible.
    }

    @Override
    protected void prepareMenuItems() {
        player.entity().getInventory().clear();

        for (MenuItem item : this.items) {
            ItemStackMenuItem menuItem = (ItemStackMenuItem) item;
            ItemStack itemStack = menuItem.getItemStack();

            player.entity().getInventory().setItem(menuItem.getSlot(), itemStack);
        }
    }
}
