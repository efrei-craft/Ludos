package fr.efreicraft.ludos.core.players.menus;

import fr.efreicraft.ludos.core.players.menus.interfaces.Menu;

/**
 * Classe pour les menus joueur.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class PlayerMenu {

    private Menu menu = null;

    public void set(Menu menu) {
        this.menu = menu;
    }

    public Menu get() {
        return menu;
    }

}
