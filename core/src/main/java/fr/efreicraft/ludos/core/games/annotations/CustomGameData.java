package fr.efreicraft.ludos.core.games.annotations;

/**
 * Annotation pour customiser le comportement du core en fonction du jeu
 * @author Aurélien D. {@literal <aurelien.dasse@efrei.net>}
 */
public @interface CustomGameData {

    /**
     * Spécifie les titles de mort si respawnTimer
     * @return tableau des titles de mort
     */
    String[] respawnTitles() default { "Vous êtes mort !" };

}
