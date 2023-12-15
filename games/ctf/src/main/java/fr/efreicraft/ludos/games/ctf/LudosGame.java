package fr.efreicraft.ludos.games.ctf;


import fr.efreicraft.ecatup.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import org.bukkit.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ewenn BAUDET
 */
@GameMetadata(
        name = "CTF",
        description = "TODO !",
        authors = {"orwenn"},
        color = "&f",
        rules = @GameRules(
                allowRespawn = true,
                minPlayers = 2,
                maxPlayers = 8
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

    public LudosGame() {
        super();
        gameLogic = new GameLogic(this);
        this.setEventListener(new EventListener(gameLogic));
    }

    @Override
    public void preMapParse(World world) {
        gameLogic.setWorld(world);
    }

    @Override
    public void beginGame() {
        super.beginGame();
        GameTimer gt = new GameTimer(gameLogic::spawnParticles, -1, 10);
    }

    @Override
    public void postMapParse() {
        Map<String, ArrayList<GamePoint>> gamePoints = Core.get().getMapManager().getCurrentMap().getGamePoints();

        gameLogic.initFlags(
                gamePoints.get("RED_FLAG").get(0).getLocation(),
                gamePoints.get("BLUE_FLAG").get(0).getLocation()
        );

        if(gamePoints.get("KILL_ZONE") != null) {
            gameLogic.initKillZone(gamePoints.get("KILL_ZONE").get(0).getLocation().getBlockY());
        }
    }

    @Override
    public void setupScoreboard(LudosPlayer player) {
        player.getBoard().clearFields();

        player.getBoard().setField(
                0,
                new ScoreboardField("&c&lRouge", true, player1 -> gameLogic.getScore("Rouge")+"")
        );

        player.getBoard().setField(
                1,
                new ScoreboardField("&9&lBleu", true, player1 -> gameLogic.getScore("Bleu")+"")
        );
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        gamePointsMaterials.put(Material.RED_CONCRETE, "RED_FLAG");
        gamePointsMaterials.put(Material.BLUE_CONCRETE, "BLUE_FLAG");
        gamePointsMaterials.put(Material.BEDROCK, "KILL_ZONE");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("RED", new TeamRecord(
                "Rouge",
                1,
                true,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.RED),
                player -> true,
                this.gameLogic::preparePlayerToSpawn
        ));
        teams.put("BLUE", new TeamRecord(
                "Bleu",
                2,
                true,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.BLUE),
                player -> true,
                this.gameLogic::preparePlayerToSpawn
        ));
        teams.putAll(DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR.getTeamRecords());
        return teams;
    }
}
