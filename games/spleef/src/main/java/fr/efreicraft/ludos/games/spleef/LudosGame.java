package fr.efreicraft.ludos.games.spleef;

import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.games.interfaces.GameMetadata;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.Map;

/**
 * Spleef game entrypoint
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Minigames/Spleef
 */

@GameMetadata(
        name = "Spleef",
        color = "&e",
        description = "Détruisez le sol et éliminez vos adversaires !",
        authors = {"Antoine"},
        version = "1.0",
        minPlayers = 1
)
public class LudosGame extends Game {

    /**
     * Constructeur du jeu.
     */
    public LudosGame() {
        super();
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
        return DefaultTeamRecordBuilder.DefaultTeamRecords.DEFAULT_TEAMS_SOLO.getTeamRecords();
    }
}
