package fr.efreicraft.ludos.core.games.annotations;

/**
 * Annotation pour les règles de fonctionnement du jeu.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public @interface GameRules {

    /**
     * Joueurs minimum pour le jeu.
     * @return Joueurs minimum pour le jeu.
     */
    int minPlayers() default 2;

    /**
     * Joueurs maximum pour le jeu.
     * @return Joueurs maximum pour le jeu.
     */
    int maxPlayers() default 20;

    /**
     * Autorise les joueurs à rejoindre en tant que spectateur depuis le lobby.
     * Une fois la partie terminée, les joueurs ayant rejoint en tant que spectateur seront kick.
     * @return Booléen d'autorisation.
     */
    boolean allowEphemeralPlayers() default true;

    /**
     * Respawn le joueur s'il meurt. S'il meurt, il passera dans l'équipe des spectateurs sans être kick à la fin de la partie.
     * @return Respawn le joueur s'il meurt.
     */
    boolean allowRespawn() default false;

    /**
     * Temps que le joueur doit attendre avant de pouvoir respawn.
     * @return Temps en secondes.
     */
    int respawnTimer() default 5;

}