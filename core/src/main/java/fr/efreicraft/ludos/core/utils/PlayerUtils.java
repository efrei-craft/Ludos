package fr.efreicraft.ludos.core.utils;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;

public class PlayerUtils {

    /**
     * Séparateur utilisé pour les titles et subtitles
     */
    public static final String SPLITTER = "\u00A7";

    private PlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void deathTitles(Player player){
        String[] intermediaire = Core.get().getGameManager().getCurrentGame().getMetadata().customData().deathTitles();
        String deathTitle = intermediaire[Core.get()
                .getGameManager().getCurrentGame().getRandom().nextInt(intermediaire.length)];

        // j'ai pas compris pourquoi 1 tick de stay, je te fais confiance Aurélien
        String[] toBeDisplayed = deathTitle.split(SPLITTER);
        if (toBeDisplayed.length <= 1 || toBeDisplayed[1].isBlank()) {
            player.sendTitle(
                    "&c" + deathTitle,
                    "",
                    0,
                    1,
                    0
            );
        } else {
            player.sendTitle(
                    "&c" + toBeDisplayed[0],
                    "&7" + toBeDisplayed[1],
                    0,
                    1,
                    0
            );
        }
    }
}
