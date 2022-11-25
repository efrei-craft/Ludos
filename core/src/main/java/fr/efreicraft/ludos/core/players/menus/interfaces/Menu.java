package fr.efreicraft.ludos.core.players.menus.interfaces;

import fr.efreicraft.ludos.core.players.Player;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Interface pour les menus.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public abstract class Menu {

    protected Player player;

    protected Component menuName;

    protected List<MenuItem> items;

    protected Menu(Player player, Component menuName, List<MenuItem> items) {
        this.player = player;
        this.menuName = menuName;
        this.items = items;
    }

    public abstract void show();

    public abstract void close();

    protected abstract void prepareMenuItems();

    public void setMenuName(Component menuName) {
        this.menuName = menuName;
    }

    public void setItems(List<MenuItem> items) {
        this.items = items;
        this.prepareMenuItems();
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public Map<UUID, MenuItem> getItemsMap() {
        return items.stream().collect(Collectors.toMap(MenuItem::getUUID, item -> item));
    }

}
