package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import fr.efreicraft.ludos.core.players.menus.interfaces.IMenuClickCallback;
import fr.efreicraft.ludos.core.players.menus.interfaces.IMenuItemRefresh;
import fr.efreicraft.ludos.core.utils.NBTUtils;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class ItemStackMenuItem extends MenuItem {

    private final ItemStack itemStack;

    private int slot;

    public ItemStackMenuItem(int slot, IMenuItemRefresh refresh, IMenuClickCallback callback) {
        super(callback, refresh);
        this.slot = slot;
        this.itemStack = ((ItemStackMenuItem) refresh.run()).getItemStack();
    }

    public ItemStackMenuItem(ItemStack itemStack, String name, String description) {
        super(name, description);
        this.itemStack = itemStack;
    }

    public ItemStackMenuItem(
            ItemStack itemStack,
            int slot,
            String name,
            String description,
            IMenuClickCallback callback
    ) {
        super(name, description, callback, null);
        this.itemStack = itemStack;
        this.slot = slot;
    }

    public ItemStack getItemStack() {
        if(this.getRefresh() != null) {
            ItemStack itemStack1 = ((ItemStackMenuItem) this.getRefresh().run()).getItemStack();
            NBTUtils.addNBT(itemStack1, "menu_item_uuid", this.getUUID().toString());
            return itemStack1;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.displayName(getName());
        itemMeta.lore(getDescription());
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        itemStack.setItemMeta(itemMeta);
        NBTUtils.addNBT(itemStack, "menu_item_uuid", this.getUUID().toString());
        return itemStack;
    }

    public int getSlot() {
        return slot;
    }

}
