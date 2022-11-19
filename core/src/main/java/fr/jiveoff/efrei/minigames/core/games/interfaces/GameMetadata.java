package fr.jiveoff.efrei.minigames.core.games.interfaces;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation pour avoir les informations sur le jeu
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface GameMetadata {

    /**
     * Nom du jeu.
     * @return Nom du jeu.
     */
    String name();

    /**
     * Couleur du jeu.
     * @return Couleur du jeu.
     */
    String color();

    /**
     * Description du jeu.
     * @return Description du jeu.
     */
    String description();

    /**
     * Auteurs du jeu.
     * @return Auteurs du jeu.
     */
    String[] authors();

    /**
     * Version du jeu.
     * @return Version du jeu.
     */
    String version();

    /**
     * Joueurs minimum pour le jeu.
     * @return Joueurs minimum pour le jeu.
     */
    int minPlayers() default 2;

    /**
     * Respawn le joueur s'il meurt.
     * @return Respawn le joueur s'il meurt.
     */
    boolean allowRespawn() default false;

}
