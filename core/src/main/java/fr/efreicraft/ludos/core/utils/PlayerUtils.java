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

        String[] toBeDisplayed = deathTitle.split(SPLITTER);
        if (toBeDisplayed.length <= 1 || toBeDisplayed[1].isBlank()) {
            player.sendTitle(
                    "&c" + deathTitle,
                    "",
                    0.2f,
                    3,
                    0.2f
            );
        } else {
            player.sendTitle(
                    "&c" + toBeDisplayed[0],
                    "&7" + toBeDisplayed[1],
                    0.2f,
                    2,
                    0.2f
            );
        }
    }
}
