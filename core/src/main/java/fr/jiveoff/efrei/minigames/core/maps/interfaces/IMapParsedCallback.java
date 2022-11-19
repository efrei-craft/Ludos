package fr.jiveoff.efrei.minigames.core.maps.interfaces;

import fr.jiveoff.efrei.minigames.core.maps.ParsedMap;

/**
 * Interface fonctionnelle pour les callbacks de parsing de map.
 * Permet d'exécuter du code après le parsing de la map.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
@FunctionalInterface
public interface IMapParsedCallback {

    /**
     * Méthode appelée après le parsing de la map.
     * @param parsedMap Map parsée.
     */
    void onMapParsed(ParsedMap parsedMap);

}