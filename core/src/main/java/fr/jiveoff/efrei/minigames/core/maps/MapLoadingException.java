package fr.jiveoff.efrei.minigames.core.maps;

/**
 * Exception levée si le chargement d'une map échoue.
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class MapLoadingException extends Exception {

    public MapLoadingException(String message) {
        super(message);
    }

}
