package fr.efreicraft.ludos.core.games.exceptions;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GameStatusException extends Exception {

    /**
     * Constructeur de GameStatusException.
     * @param message Message d'erreur.
     */
    public GameStatusException(String message) {
        super(message);
    }

}
