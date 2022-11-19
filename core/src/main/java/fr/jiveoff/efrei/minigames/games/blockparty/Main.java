package fr.jiveoff.efrei.minigames.games.blockparty;

import fr.jiveoff.efrei.minigames.core.Core;
import fr.jiveoff.efrei.minigames.core.games.interfaces.Game;
import fr.jiveoff.efrei.minigames.core.games.interfaces.GameMetadata;
import fr.jiveoff.efrei.minigames.core.players.Player;
import fr.jiveoff.efrei.minigames.core.players.scoreboards.ScoreboardField;
import fr.jiveoff.efrei.minigames.core.teams.DefaultTeamRecordBuilder;
import fr.jiveoff.efrei.minigames.core.teams.TeamRecord;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.Map;

/**
 * BlockParty game entrypoint
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @author Logan T. {@literal <logane.tann@efrei.net>}
 * @project Minigames/BlockParty
 */

@GameMetadata(
        name = "BlockParty",
        color = "&b",
        description = "Tenez-vous sur la bonne couleur au bon moment, sinon vous mourrez !",
        authors = {"Antoine", "Logan"},
        version = "1.0",
        minPlayers = 1
)
public class Main extends Game {

    private final GameLogic gameLogic;

    /**
     * Constructeur du jeu.
     */
    public Main() {
        super();
        this.gameLogic = new GameLogic();
        this.setEventListener(new EventListener(this.gameLogic));
    }

    @Override
    public void preMapParse(World world) {
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }

    @Override
    public void postMapParse() {
        Location killZoneLocation = Core.getInstance().getMapManager().getCurrentMap().getGamePoints()
                .get("KILL_ZONE").get(0).getLocation();
        this.gameLogic.setKillZoneLocation(killZoneLocation);
        Core.getInstance().getMapManager().getCurrentMap().getWorld().setBlockData(
                killZoneLocation,
                Material.PINK_WOOL.createBlockData()
        );

        this.gameLogic.generateDanceFloor(Core.getInstance().getMapManager().getCurrentMap().getGamePoints().get("DANCE_FLOOR"));
    }

    @Override
    public void setupScoreboard(Player player) {
        player.getBoard().clearFields();

        player.getBoard().setField(
                0,
                new ScoreboardField("&b&lJoueurs en vie", player, true, player1 -> String.valueOf(Core.getInstance().getTeamManager().getTeam("PLAYERS").getPlayers().size()))
        );
        player.getBoard().setField(
                1,
                new ScoreboardField("&b&lIntervalle", "TODO")
        );
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        gamePointsMaterials.put(Material.BEDROCK, "KILL_ZONE");
        gamePointsMaterials.put(Material.REDSTONE_BLOCK, "DANCE_FLOOR");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        return DefaultTeamRecordBuilder.DefaultTeamRecords.DEFAULT_TEAMS_SOLO.getTeamRecords();
    }
}
