package fr.jiveoff.efrei.minigames.games.arena;

import fr.jiveoff.efrei.minigames.core.games.interfaces.Game;
import fr.jiveoff.efrei.minigames.core.games.interfaces.GameMetadata;
import fr.jiveoff.efrei.minigames.core.players.Player;
import fr.jiveoff.efrei.minigames.core.teams.DefaultTeamRecordBuilder;
import fr.jiveoff.efrei.minigames.core.teams.TeamRecord;
import fr.jiveoff.efrei.minigames.core.utils.ColorUtils;
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
        description = "Un 4v4 dans un arène, bonne chance !",
        authors = {"Antoine"},
        version = "1.0",
        minPlayers = 1,
        allowRespawn = true
)
public class Main extends Game {

    private final GameLogic gameLogic;

    /**
     * Constructeur du jeu.
     */
    public Main() {
        super();
        this.gameLogic = new GameLogic();
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
    public void setupScoreboard(Player player) {
        player.getBoard().clearFields();
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        return new EnumMap<>(Material.class);
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        HashMap<String, TeamRecord> teams = new HashMap<>();
        teams.put("BARBARES", new TeamRecord(
                "Barbares",
                1,
                true,
                true,
                new ColorUtils.TeamColorSet(ColorUtils.TeamColors.RED),
                this.gameLogic::preparePlayerToSpawn
        ));
        teams.put("VIKINGS", new TeamRecord(
                "Vikings",
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
