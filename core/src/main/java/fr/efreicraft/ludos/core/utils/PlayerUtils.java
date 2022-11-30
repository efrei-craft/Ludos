package fr.efreicraft.ludos.core.utils;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.Player;

public class PlayerUtils {

    private PlayerUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void deathTitles(Player player){
        String[] intermediaire = Core.get().getGameManager().getCurrentGame().getMetadata().customData().deathTitles();
        String deathTitle = intermediaire[Core.get()
                .getGameManager().getCurrentGame().getRandom().nextInt(intermediaire.length)];
        player.sendTitle(
                "&c" + deathTitle,
                "",
                0,
                1,
                0
        );
    }
}
