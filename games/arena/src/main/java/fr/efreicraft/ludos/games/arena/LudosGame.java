package fr.efreicraft.ludos.games.arena;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.CustomGameData;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Jeu Arena pour tester les équipes.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */

@GameMetadata(
        name = "Arena",
        color = "&c",
        description = "L'équipe avec le plus de kills à la fin du timer gagne !",
        authors = {"Antoine"},
        rules = @GameRules(
                allowRespawn = true,
                respawnTimer = 5,
                minPlayers = 2,
                minPlayersToStart = 2,
                maxPlayers = 8
        ),
        customData = @CustomGameData(
                respawnTitles = {"Zut Alors !", "C'est pas fini !", "Relève-toi !", "Mieux que ça ..."}
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
        this.setEventListener(new EventListener(gameLogic));
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
        // Rien à faire ici
    }

    @Override
    public void beginGame() {
        super.beginGame();
        gameLogic.startTimer();
    }

    @Override
    public void endGame() {
        gameLogic.stopTimer();
        super.endGame();
    }

    @Override
    public void setupScoreboard(Player player) {
        player.getBoard().clearFields();

        player.getBoard().setField(
                0,
                new ScoreboardField("&c&lKills Vikings", player, true, player1 -> String.valueOf(gameLogic.getTeamKills(Core.get().getTeamManager().getTeam("VIKINGS"))))
        );

        player.getBoard().setField(
                1,
                new ScoreboardField("&9&lKills Romains", player, true, player1 -> String.valueOf(gameLogic.getTeamKills(Core.get().getTeamManager().getTeam("ROMAINS"))))
        );

        player.getBoard().setField(
                2,
                new ScoreboardField(
                        "&6&lTimer",
                        player,
                        false,
                        player1 -> gameLogic.getTimerString()
                )
        );
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        return new EnumMap<>(Material.class);
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("VIKINGS", new TeamRecord(
                "Vikings",
                1,
                true,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.RED),
                this.gameLogic::preparePlayerToSpawn
        ));
        teams.put("ROMAINS", new TeamRecord(
                "Romains",
                2,
                true,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.BLUE),
                this.gameLogic::preparePlayerToSpawn
        ));
        teams.putAll(DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR.getTeamRecords());
        return teams;
    }

}
