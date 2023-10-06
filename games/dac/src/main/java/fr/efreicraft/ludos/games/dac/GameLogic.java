package fr.efreicraft.ludos.games.dac;

import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Location;

public class GameLogic {
    private int bassinPositionY;

    public void setBassinLocation(Location bassinLocation) {
        this.bassinPositionY = bassinLocation.getBlockY();
    }

    public boolean isInWater(double positionY) {
        return bassinPositionY < positionY;
    }

    public void onPlayerInWater(LudosPlayer player) {


    }

    public void onPlayerTouchingGround(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Vous êtes tombé sur un bloc ! Vous êtes éliminé !");

    }
}
