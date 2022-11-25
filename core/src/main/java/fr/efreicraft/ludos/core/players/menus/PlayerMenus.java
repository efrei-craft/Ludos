package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.players.menus.interfaces.Menu;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Classe pour les interactions des joueurs avec les différents menus.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class PlayerMenus {

    private final Map<String, Menu> menus = new HashMap<>();

    public MenuItem getMenuItemFromUUID(UUID uuid) {
        for (Menu menu : this.menus.values()) {
            if (menu.getItemsMap().containsKey(uuid)) {
                return menu.getItemsMap().get(uuid);
            }
        }
        return null;
    }

    public Menu setMenu(String name, Menu menu) {
        this.menus.put(name, menu);
        return menu;
    }

    public Menu getMenu(String name) {
        return this.menus.get(name);
    }

}
