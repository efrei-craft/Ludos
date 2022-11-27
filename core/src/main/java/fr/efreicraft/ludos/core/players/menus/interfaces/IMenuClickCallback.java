package fr.efreicraft.ludos.core.players.menus.interfaces;

import org.bukkit.event.Event;

/**
 * Interface fonctionnelle permettant de définir une action à effectuer lors d'un clic sur un item d'un menu.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public interface IMenuClickCallback {

    void run(Event event);

}