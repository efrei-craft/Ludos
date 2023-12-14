package fr.efreicraft.ludos.games.sumo;

import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;

public class GameLogic {
    private int killZonePositionY;

    private final Map<LudosPlayer, Integer> playerKills = new HashMap<>();

    public void setKillZoneLocation(Location killZoneLocation) {
        this.killZonePositionY = killZoneLocation.getBlockY();
    }

    public boolean isOutsideKillzone(double positionY) {
        return killZonePositionY > positionY;
    }

    public void onPlayerBelowKillzone(LudosPlayer player) {
        player.entity().setHealth(0);

        LudosPlayer otherPlayer = player.getTeam().getPlayers().stream()
                .filter(p -> p != player)
                .findFirst()
                .orElse(null);

        if (otherPlayer != null) {
            this.playerKills.put(otherPlayer, this.playerKills.getOrDefault(otherPlayer, 0) + 1);
        }
    }

    public int getPlayerKills(LudosPlayer player) {
        return this.playerKills.getOrDefault(player, 0);
    }

    public boolean canPlayerRespawn(LudosPlayer player) {
        LudosPlayer otherPlayer = player.getTeam().getPlayers().stream()
                .filter(p -> p != player)
                .findFirst()
                .orElse(null);

        if (otherPlayer != null) {
            return this.playerKills.getOrDefault(otherPlayer, 0) < 2;
        }

        return true;
    }
}
