package fr.efreicraft.ludos.core.games.annotations;

/**
 * Annotation pour customiser le comportement du core en fontction du jeu
 */
public @interface CustomGameData {

    /**
     * Spécifie les titles de mort si respawnTimer
     * @return tableau des titles de mort
     */
    String[] respawnTitles() default { "Vous êtes mort !" };

}
