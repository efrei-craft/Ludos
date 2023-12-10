package fr.efreicraft.ludos.games.dac;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import fr.efreicraft.ludos.core.utils.TitleUtils;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {

    private final List<LudosPlayer> players = new ArrayList<>();
    private Location bassinPosition;
    private Location plateformePosition;
    private Location spawnPosition;
    private Location boundary1Position;
    private Location boundary2Position;
    private int round = 1;
    private int numberOfPlayers;
    private double limitOfPlayers;

    public void setBassinLocation(Location bassinLocation) {
        this.bassinPosition = bassinLocation;
    }

    public void setPlateformeLocation(Location plateformeLocation) {
        this.plateformePosition = plateformeLocation.getBlock().getLocation();
        this.plateformePosition.setPitch(0);
        this.plateformePosition.setYaw(180);
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public void setLimitOfPlayers(int numberOfPlayers) {
        this.limitOfPlayers = Math.ceil((double) numberOfPlayers / 2);
    }

    public void setPlateformeBoundaries(Location boundary1Location, Location boundary2Location) {
        this.boundary1Position = boundary1Location;
        this.boundary2Position = boundary2Location;
    }

    public void setSpawnPosition(Location spawnLocation) {
        this.spawnPosition = spawnLocation;
    }

    public boolean isInBassin(Location position) {
        return bassinPosition.getBlockY() > position.getY() & bassinPosition.getBlockY() - 2 < position.getY() &
                bassinPosition.getBlockX() + 4 > position.getX() & bassinPosition.getBlockX() - 4 < position.getX() &
                bassinPosition.getBlockZ() + 4 > position.getZ() & bassinPosition.getBlockZ() - 4 < position.getZ();
    }

    public boolean isOnGround(double positionY) {
        return bassinPosition.getBlockY() == positionY;
    }

    public void onPlayerInBassin(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Tombé dans l'eau");
        Core.get().getMapManager().getCurrentMap().getWorld().setBlockData(new Location(
                Core.get()
                    .getMapManager()
                    .getCurrentMap()
                    .getWorld(),
                player.entity()
                      .getLocation()
                      .getBlockX(),
                bassinPosition.getY() - 1,
                player.entity()
                      .getLocation()
                      .getBlockZ()
        ), Material.BLACK_CONCRETE.createBlockData());
        player.entity().teleport(this.spawnPosition);
        nextPlayer(player);
    }

    public void movePlateforme(int round) {
        if (round != 1) {
            int decalagePlateforme = 30 * (round - 1);
            for (int y = this.boundary2Position.getBlockY(); y >= this.boundary1Position.getBlockY(); y--) {
                for (int z = this.boundary2Position.getBlockZ(); z <= this.boundary1Position.getBlockZ(); z++) {
                    for (int x = this.boundary2Position.getBlockX(); x <= this.boundary1Position.getBlockX(); x++) {
                        Core.get()
                            .getMapManager()
                            .getCurrentMap()
                            .getWorld()
                            .getBlockAt(x, y + decalagePlateforme, z)
                            .setType(Core.get()
                                         .getMapManager()
                                         .getCurrentMap()
                                         .getWorld()
                                         .getBlockAt(x, y, z)
                                         .getType());
                        Core.get().getMapManager().getCurrentMap().getWorld().getBlockAt(x, y, z).setType(Material.AIR);
                    }
                }
            }
            this.plateformePosition.setY(this.plateformePosition.getY() + decalagePlateforme);
        }
    }

    public void onPlayerTouchingGround(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Vous êtes tombé sur une bloc ! Vous êtes éliminé !");
        player.setTeam(Core.get().getTeamManager().getTeam("SPECTATORS"));
        player.entity().setGameMode(org.bukkit.GameMode.SPECTATOR);
        players.remove(player);
        nextPlayer(player);
    }

    public void nextPlayer(LudosPlayer player) {
        player.sendMessage(MessageUtils.ChatPrefix.GAME, "Il reste " + players.size() + " joueurs");
        if (players.size() == 1) {
            TitleUtils.broadcastTitle("&9" + "Victoire de " + players.get(0).entity().getName(), "", 0, 2, 0.5f);
        }
        else if (players.size() < limitOfPlayers + Math.abs(round % 2 - 1)) {
            this.round++;
            playRound(this.round);
        }
        else {
            for (LudosPlayer currentPlayer : players) {
                if (currentPlayer == player && players.lastIndexOf(player) < players.size()) {
                    players.iterator().next().entity().teleport(this.plateformePosition);
                }
                else {
                    players.get(0).entity().teleport(this.plateformePosition);
                }
            }
        }
    }

    public void setPlayingPlayers() {
        this.players.addAll(Core.get().getTeamManager().getTeam("PLAYERS").getPlayers());
    }

    public void playRound(int round) {
        TitleUtils.broadcastTitle(
                Core.get().getGameManager().getCurrentGame().getMetadata().color() + "Round " + round,
                "",
                0,
                2,
                0.5f
        );
        movePlateforme(round);
        players.get(0).entity().teleport(this.plateformePosition);
    }

    public void onGameStart() {
        setNumberOfPlayers(Core.get().getTeamManager().getTeam("PLAYERS").getPlayers().size());
        setLimitOfPlayers(numberOfPlayers);
        setPlayingPlayers();
        playRound(1);
    }
}
