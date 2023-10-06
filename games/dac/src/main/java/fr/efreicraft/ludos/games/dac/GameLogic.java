package fr.efreicraft.ludos.games.dac;

import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Location;

public class GameLogic {
    private int prismarinePositionY;

    public void setPrismarineLocation(Location prismarineLocation) {
        this.prismarinePositionY = prismarineLocation.getBlockY();
    }

    public boolean isInWater(double positionY) {
        return prismarinePositionY < positionY;
    }

    public void onPlayerInWater(LudosPlayer player) {


    }
    public void onPlayerTouchingGround(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Vous êtes tombé sur un bloc ! Vous êtes éliminé !");

    }
}
