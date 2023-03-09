package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ecatup.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.CustomGameData;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;

import java.util.*;

/**
 * Implémentation de la classe {@link Game}.
 * @author Idir NAIT MEDDOUR
 */
@GameMetadata(
        name = "Rush",
        color = "&5",
        description = "Détruisez le lit de l'équipe adversaire, puis tuez-les pour remporter la victoire !",
        authors = {"Niilyx"},
        rules = @GameRules(
                allowRespawn = true,
                minPlayersToStart = 2,
                maxPlayers = 16,
                allowEphemeralPlayers = true
        ),
        customData = @CustomGameData(
                respawnTitles = {"Regarde des 2 côtés !!!", "ezclap", "Oof", "Vous êtes mort !"}
        )
)
public class LudosGame extends Game {

    private final GameLogic gameLogic;

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
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        this.gameLogic.world(world);
    }

    @Override
    public void postMapParse() {
        Location mid = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();
        Location killZoneLocation = new Location(
                mid.getWorld(),
                mid.getX(),
                mid.getY() - 100,
                mid.getZ()
        );

        Core.get().getMapManager().getCurrentMap().setMiddleOfMap(
                Core.get().getMapManager().getCurrentMap().getGlobalPoints().get("MIDDLE").get(0).getLocation()
        );
        this.gameLogic.yDeath(killZoneLocation.getBlockY());

        Team BLEUS = Utils.getTeam("BLEUS");
        Team ROUGES = Utils.getTeam("ROUGES");
        Team VERTS = Utils.getTeam("VERTS");
        Team JAUNES = Utils.getTeam("JAUNES");

        this.gameLogic.TEAMS_ITEMSPAWNERS.put(BLEUS, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM1_GENERATOR"));
        this.gameLogic.TEAMS_ITEMSPAWNERS.put(ROUGES, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM2_GENERATOR"));
        this.gameLogic.TEAMS_ITEMSPAWNERS.put(VERTS, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM3_GENERATOR"));
        this.gameLogic.TEAMS_ITEMSPAWNERS.put(JAUNES, Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM4_GENERATOR"));

        this.gameLogic.TEAMS_BED[0] = Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM1_BED").get(0);
        this.gameLogic.TEAMS_BED[1] = Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM2_BED").get(0);
        this.gameLogic.TEAMS_BED[2] = Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM3_BED").get(0);
        this.gameLogic.TEAMS_BED[3] = Core.get().getMapManager().getCurrentMap().getGamePoints().get("TEAM4_BED").get(0);
    }

    @Override
    public void beginGame() {
        super.beginGame();

        gameLogic.startStopwatch();
        gameLogic.setupMerchants();
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
    public void setupScoreboard(LudosPlayer player) {
        player.getBoard().clearFields();

        List<Team> teamList = Core.get().getTeamManager().getPlayingTeams().values()
                .stream()
                .sorted(Comparator.comparingInt(Team::getPriority))
                .toList();

        for (int i = 0, teamListSize = teamList.size(); i < teamListSize; i++) {
            Team team = teamList.get(i);
            if (team == null || team.getPlayers().isEmpty()) continue;

            player.getBoard().setField(i, new ScoreboardField(
                    LegacyComponentSerializer.legacyAmpersand().serialize(team.name()),
                    true,
                    (player1) -> {
                        if (!gameLogic.bedDestroyed(team))
                            return "&a\u2713";
                        else
                            return "&7" + team.getPlayers().size();
                    }
            ));
        }

    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);

        /* Le générateur d'items des teams */
        gamePointsMaterials.put(Material.BLUE_WOOL, "TEAM1_GENERATOR");
        gamePointsMaterials.put(Material.RED_WOOL, "TEAM2_GENERATOR");
        gamePointsMaterials.put(Material.LIME_WOOL, "TEAM3_GENERATOR");
        gamePointsMaterials.put(Material.YELLOW_WOOL, "TEAM4_GENERATOR");

        /* Les lits */
        gamePointsMaterials.put(Material.BLUE_CONCRETE, "TEAM1_BED");
        gamePointsMaterials.put(Material.RED_CONCRETE, "TEAM2_BED");
        gamePointsMaterials.put(Material.LIME_CONCRETE, "TEAM3_BED");
        gamePointsMaterials.put(Material.YELLOW_CONCRETE, "TEAM4_BED");

        /* Traders de cryptomonnaies */
        gamePointsMaterials.put(Material.SANDSTONE, "MERCHANT_BATISSEUR");
        gamePointsMaterials.put(Material.CHISELED_SANDSTONE, "MERCHANT_TERRORISTE");
        gamePointsMaterials.put(Material.CUT_SANDSTONE, "MERCHANT_TAVERNIER");
        gamePointsMaterials.put(Material.SMOOTH_SANDSTONE, "MERCHANT_ARMURIER");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("BLEUS", new TeamRecord(
                "Bleus",
                1,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.BLUE),
                (player) -> !gameLogic.bedDestroyed(player.getTeam()),
                gameLogic::preparePlayerToSpawn
        ));
        teams.put("ROUGES", new TeamRecord(
                "Rouges",
                2,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.RED),
                (player) -> !gameLogic.bedDestroyed(player.getTeam()),
                gameLogic::preparePlayerToSpawn
        ));
        teams.put("VERTS", new TeamRecord(
                "Verts",
                3,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.GREEN),
                (player) -> !gameLogic.bedDestroyed(player.getTeam()),
                gameLogic::preparePlayerToSpawn
        ));
        teams.put("JAUNES", new TeamRecord(
                "Jaunes",
                4,
                false,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.YELLOW),
                (player) -> !gameLogic.bedDestroyed(player.getTeam()),
                gameLogic::preparePlayerToSpawn
        ));
        teams.putAll(DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR.getTeamRecords());
        return teams;
    }
}
