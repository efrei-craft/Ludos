package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.players.menus.interfaces.IMenuClickCallback;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import fr.efreicraft.ludos.core.players.menus.interfaces.IMenuItemRefresh;
import org.bukkit.inventory.ItemStack;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class ChestMenuItem extends MenuItem {

    private final ItemStack itemStack;

    private final Integer slot;

    public ChestMenuItem(ItemStack itemStack, Integer slot, IMenuItemRefresh refresh, IMenuClickCallback callback) {
        super(callback, refresh);
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public ChestMenuItem(ItemStack itemStack, String name, String description) {
        super(name, description);
        this.itemStack = itemStack;
        this.slot = null;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public Integer getSlot() {
        return slot;
    }

}
