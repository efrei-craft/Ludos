package fr.efreicraft.ludos.games.sumo;

import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Location;

public class GameLogic {
    private int killZonePositionY;

    public void setKillZoneLocation(Location killZoneLocation) {
        this.killZonePositionY = killZoneLocation.getBlockY();
    }

    public boolean isOutsideKillzone(double positionY) {
        return killZonePositionY > positionY;
    }

    public void onPlayerBelowKillzone(LudosPlayer player) {
        player.entity().setHealth(0);
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Vous avez été éliminé !");
    }
}
