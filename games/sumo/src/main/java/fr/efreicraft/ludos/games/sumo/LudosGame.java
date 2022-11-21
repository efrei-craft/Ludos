package fr.efreicraft.ludos.games.sumo;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;

import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

@GameMetadata(
name = "Sumo",
        description = "Poussez votre adversaire en dehors du ring !",
        authors = {"DocSystem"},
        color = "&4",
        rules = @GameRules(
                minPlayers = 2,
                maxPlayers = 2
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

    public LudosGame() {
        super();
        this.gameLogic = new GameLogic();
        this.setEventListener(new EventListener(this.gameLogic));
    }

    @Override
    public void preMapParse(World world) {
        // Nothing to do here
    }

    @Override
    public void beginGame() {
        super.beginGame();
        Core.get().getTeamManager().getTeam("PLAYERS").setFriendlyFire(true);
    }

    @Override
    public void postMapParse() {
        this.gameLogic.setKillZoneLocation(Core.get().getMapManager().getCurrentMap().getLowestBoundary());
    }

    @Override
    public void setupScoreboard(Player player) {

    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        gamePointsMaterials.put(Material.BEDROCK, "KILL_ZONE");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        return new HashMap<>(DefaultTeamRecordBuilder.DefaultTeamRecords.DEFAULT_TEAMS_SOLO.getTeamRecords());
    }
}
