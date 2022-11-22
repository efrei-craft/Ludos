package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

// TODO: écrire une desc
@GameMetadata(
        name = "Rush",
        color = "&5",
        description = "Détruisez le lit de l'équipe adversaire, puis tuez-les pour remporter la victoire !",
        authors = {"Idir 'Niilyx' NAIT MEDDOUR"},
        rules = @GameRules(
                allowRespawn = true,
                minPlayersToStart = 1,
                maxPlayers = 8
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

    /**
     * Constructeur de jeu.
     */
    protected LudosGame() {
        super();
        this.gameLogic = new GameLogic();
        this.setEventListener(new EventListener(gameLogic));
    }

    @Override
    public void preMapParse(World world) {
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, true);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
    }

    @Override
    public void postMapParse() {
        Location killZoneLocation = Core.get().getMapManager().getCurrentMap().getGamePoints()
                .get("KILL_ZONE").get(0).getLocation();
        this.gameLogic.yDeath(killZoneLocation.getBlockY());
    }

    @Override
    public void setupScoreboard(Player player) {

    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        /** Le générateur */
        gamePointsMaterials.put(Material.REDSTONE_BLOCK, "TEAM1_GENERATOR");
        gamePointsMaterials.put(Material.LAPIS_BLOCK, "TEAM2_GENERATOR");
        gamePointsMaterials.put(Material.CUT_SANDSTONE, "MERCHANT");
        gamePointsMaterials.put(Material.PINK_WOOL, "KILL_ZONE");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("ORANGES", new TeamRecord(
                "Oranges",
                1,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.GOLD),
                null
        ));
        teams.put("VERTS", new TeamRecord(
                "Verts",
                2,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.GREEN),
                null
        ));
        teams.putAll(DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR.getTeamRecords());
        return teams;
    }
}
