package fr.efreicraft.ludos.games.dac;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public class GameLogic {
    private Location bassinPosition;
    private Location plateformePosition;

    private Location spawnPosition ;

    private Location boundary1Position;

    private Location boundary2Position;

    private int round = 2;



    public void setBassinLocation(Location bassinLocation) {
        this.bassinPosition = bassinLocation;
    }
    public void setPlateformeLocation(Location plateformeLocation) {
        this.plateformePosition = plateformeLocation.getBlock().getLocation();
        this.plateformePosition.setPitch(0);
        this.plateformePosition.setYaw(180);
    }
    public void setPlateformeBoundaries(Location boundary1Location, Location boundary2Location) {
        this.boundary1Position = boundary1Location;
        this.boundary2Position = boundary2Location;
    }

    public void setSpawnPosition(Location spawnLocation) {
        this.spawnPosition = spawnLocation;
    }
    public boolean isInBassin(Location position) {
        return bassinPosition.getBlockY() > position.getY() & bassinPosition.getBlockY()-2<position.getY() & bassinPosition.getBlockX() +4> position.getX() & bassinPosition.getBlockX() -4< position.getX() & bassinPosition.getBlockZ() +4> position.getZ() & bassinPosition.getBlockZ() -4< position.getZ();
    }

    public boolean isOnGround(double positionY){return bassinPosition.getBlockY()==positionY;}

    public void onPlayerInBassin(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Tombé dans l'eau");
        Core.get().getMapManager().getCurrentMap().getWorld().setBlockData(new Location(Core.get().getMapManager().getCurrentMap().getWorld(),player.entity().getLocation().getBlockX(),bassinPosition.getY()-1,player.entity().getLocation().getBlockZ()), Material.BLACK_CONCRETE.createBlockData());
        movePlateforme(this.round);
        player.entity().teleport(this.spawnPosition);

    }

    public void movePlateforme(int round){
        int decalagePlateforme=30*(round-1);
        for (int y=this.boundary2Position.getBlockY();y>=this.boundary1Position.getBlockY();y--) {
            for (int z = this.boundary2Position.getBlockZ(); z <= this.boundary1Position.getBlockZ(); z++) {
                for (int x = this.boundary2Position.getBlockX(); x <= this.boundary1Position.getBlockX(); x++) {
                    Core.get().getMapManager().getCurrentMap().getWorld().getBlockAt(x, y+decalagePlateforme, z).setType(Core.get().getMapManager().getCurrentMap().getWorld().getBlockAt(x, y, z).getType());
                    Core.get().getMapManager().getCurrentMap().getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                }
            }}
        this.plateformePosition.setY(this.plateformePosition.getY()+decalagePlateforme);

    }

    public void onPlayerTouchingGround(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Vous êtes tombé sur une bloc ! Vous êtes éliminé !");
    }
    public void playRound(int round){
        Set<LudosPlayer> players = Core.get().getPlayerManager().getPlayers();
        if (!(round==1)){
            movePlateforme(round);
        }
        while(players.size()<8) {
            for (LudosPlayer currentPlayer : players) {
                if (currentPlayer.getTeam().isPlayingTeam()) {
                    currentPlayer.entity().teleport(plateformePosition);
                }
            }
        }
        this.round = round;
    }
}
