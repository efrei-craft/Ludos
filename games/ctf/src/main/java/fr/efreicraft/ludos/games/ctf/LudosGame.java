package fr.efreicraft.ludos.games.ctf;


import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
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

@GameMetadata(
        name = "CTF",
        description = "TODO !",
        authors = {"orwenn"},
        color = "&f",
        rules = @GameRules(
                allowRespawn = true,
                minPlayers = 2,
                maxPlayers = 2
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

    public LudosGame() {
        super();
        gameLogic = new GameLogic();
        this.setEventListener(new EventListener(gameLogic));
    }

    @Override
    public void preMapParse(World world) {
        // Nothing to do here
    }

    @Override
    public void beginGame() {
        super.beginGame();

    }

    @Override
    public void postMapParse() {
        Map<String, ArrayList<GamePoint>> game_points = Core.get().getMapManager().getCurrentMap().getGamePoints();

        gameLogic.initFlags(
                game_points.get("RED_FLAG").get(0).getLocation(),
                game_points.get("BLUE_FLAG").get(0).getLocation()
        );
    }

    @Override
    public void setupScoreboard(LudosPlayer player) {
        // No scoreboard for now
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        gamePointsMaterials.put(Material.RED_CONCRETE, "RED_FLAG");
        gamePointsMaterials.put(Material.BLUE_CONCRETE, "BLUE_FLAG");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("RED", new TeamRecord(
                "Red",
                1,
                true,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.RED),
                player -> true,
                this.gameLogic::preparePlayerToSpawn
        ));
        teams.put("BLUE", new TeamRecord(
                "Blue",
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
