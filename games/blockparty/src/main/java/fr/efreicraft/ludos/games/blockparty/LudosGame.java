package fr.efreicraft.ludos.games.blockparty;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
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
 * @project Ludos/BlockParty
 */

@GameMetadata(
        name = "BlockParty",
        color = "&b",
        description = "Tenez-vous sur la bonne couleur au bon moment, sinon vous mourrez !",
        authors = {"Antoine", "Logan"},
        rules = @GameRules(
                minPlayers = 2
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

    /**
     * Constructeur du jeu.
     */
    public LudosGame() {
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
        Location killZoneLocation = Core.get().getMapManager().getCurrentMap().getGamePoints()
                .get("KILL_ZONE").get(0).getLocation();
        this.gameLogic.setKillZoneLocation(killZoneLocation);
        Core.get().getMapManager().getCurrentMap().getWorld().setBlockData(
                killZoneLocation,
                Material.PINK_WOOL.createBlockData()
        );
        this.gameLogic.setGamePointList();
        this.gameLogic.generateDanceFloor();
    }

    @Override
    public void setupScoreboard(Player player) {
        player.getBoard().clearFields();

        player.getBoard().setField(
                0,
                new ScoreboardField("&b&lJoueurs en vie", true, player1 -> this.gameLogic.getRemainingPlayers())
        );
        player.getBoard().setField(
                1,
                new ScoreboardField("&b&lIntervalle", "TODO")
        );
    }

    @Override
    public void beginGame() {
        super.beginGame();
        gameLogic.onGameStart();
    }

    @Override
    public void endGame() {
        this.gameLogic.destructor();
        super.endGame();
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
