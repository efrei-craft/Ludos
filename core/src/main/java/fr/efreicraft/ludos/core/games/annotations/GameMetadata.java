package fr.efreicraft.ludos.core.games.annotations;

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
     * Règles de fonctionnement du jeu.
     * @return Annotation des règles de fonctionnement du jeu.
     */
    GameRules rules() default @GameRules;


    /**
     * Customisation des comportements du Core en fonction du jeu.
     * @return Annotation des customisation.
     */
    CustomGameData customData() default @CustomGameData;

}
