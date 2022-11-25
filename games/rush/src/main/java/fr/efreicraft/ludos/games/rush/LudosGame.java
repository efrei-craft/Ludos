package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.CustomGameData;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import org.bukkit.*;

import java.util.*;

@GameMetadata(
        name = "Rush",
        color = "&5",
        description = "Détruisez le lit de l'équipe adversaire, puis tuez-les pour remporter la victoire !",
        authors = {"Idir 'Niilyx' NAIT MEDDOUR"},
        rules = @GameRules(
                allowRespawn = true,
                minPlayersToStart = 1,
                maxPlayers = 8,
                allowEphemeralPlayers = true
        ),
        customData = @CustomGameData(
                respawnTitles = {"Regarde des 2 côtés !!!", "ezclap", "Oof", "Vous êtes mort !"}
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

    /**
     * Constructeur de jeu.
     */
    public LudosGame() {
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

        this.gameLogic.world(world);
    }

    @Override
    public void postMapParse() {
        ArrayList<GamePoint> merchants = Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT");

        Location killZoneLocation = merchants.get(0).getLocation().subtract(0, 150, 0);
        this.gameLogic.yDeath(killZoneLocation.getBlockY());

        Core.get().getMapManager().getCurrentMap().setMiddleOfMap(
                Core.get().getMapManager().getCurrentMap().getGlobalPoints().get("MIDDLE").get(0).getLocation()
        );

        Team BLEUS = Utils.getTeam("BLEUS");
        Team ROUGES = Utils.getTeam("ROUGES");
        Team VERTS = Utils.getTeam("VERTS");
        Team JAUNES = Utils.getTeam("JAUNES");

        this.gameLogic.TEAMS_ITEMSPAWNERS.put(BLEUS, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM1_GENERATOR"));
        this.gameLogic.TEAMS_ITEMSPAWNERS.put(ROUGES, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM2_GENERATOR"));
        this.gameLogic.TEAMS_ITEMSPAWNERS.put(VERTS, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM3_GENERATOR"));
        this.gameLogic.TEAMS_ITEMSPAWNERS.put(JAUNES, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM4_GENERATOR"));

        // Les values de getSpawnPoints() sont des arraylist de 1 seul spawnpoint pour le rush, donc je le récupère directement.
        Core.get().getMapManager().getCurrentMap().getSpawnPoints().forEach((key, value) -> this.gameLogic.TEAMS_BED.put(key, value.get(0)));

        Bukkit.getLogger().info(Core.get().getMapManager().getCurrentMap().getMiddleOfMap().toString());
        Core.get().getMapManager().getCurrentMap().getMiddleOfMap()
    }

    @Override
    public void beginGame() {
        super.beginGame();

        gameLogic.startStopwatch();
        gameLogic.setMerchant();
        gameLogic.setupVillagers();
        gameLogic.setupBeds();
    }

    /**
     * Méthode standardisée de fin de jeu
     */
    @Override
    public void endGame() {
        gameLogic.stopStopwatch();
        super.endGame();
    }

    @Override
    public void setupScoreboard(Player player) {
        player.getBoard().clearFields();
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);

        gamePointsMaterials.put(Material.NETHERITE_BLOCK, "MIDDLE");

        /* Le générateur d'items des teams */
        gamePointsMaterials.put(Material.BLUE_WOOL, "TEAM1_GENERATOR");
        gamePointsMaterials.put(Material.RED_WOOL, "TEAM2_GENERATOR");
        gamePointsMaterials.put(Material.GREEN_WOOL, "TEAM3_GENERATOR");
        gamePointsMaterials.put(Material.YELLOW_WOOL, "TEAM4_GENERATOR");

        /* Traders de cryptomonnaies */
        gamePointsMaterials.put(Material.CUT_SANDSTONE, "MERCHANT");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("BLEUS", new TeamRecord(
                "Bleus",
                2,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.BLUE),
                null
        ));
        teams.put("ROUGES", new TeamRecord(
                "Rouges",
                1,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.RED),
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
        teams.put("JAUNES", new TeamRecord(
                "Jaunes",
                1,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.YELLOW),
                null
        ));
        teams.putAll(DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR.getTeamRecords());
        return teams;
    }
}
