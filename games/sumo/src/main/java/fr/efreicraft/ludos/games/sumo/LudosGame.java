package fr.efreicraft.ludos.games.sumo;

import com.google.common.collect.ImmutableMap;
import fr.efreicraft.ecatup.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;

import java.util.EnumMap;
import java.util.Map;

import static fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR;

@GameMetadata(
        name = "Sumo",
        description = "Poussez votre adversaire en dehors du ring dans un BO3 et remportez la partie !",
        authors = {"DocSystem et JiveOff"},
        color = "&4",
        rules = @GameRules(
                minPlayers = 2,
                maxPlayers = 2,
                allowRespawn = true
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
        this.gameLogic.setKillZoneLocation(
                Core.get().getMapManager().getCurrentMap().getGamePoints().get("KILL_ZONE").get(0).getLocation()
        );
    }

    @Override
    public void setupScoreboard(LudosPlayer player) {
        player.getBoard().clearFields();

        player.getBoard().setField(
                0,
                new ScoreboardField(
                        "&4&lScore",
                        false,
                        player1 -> {
                            LudosPlayer ludosPlayer = Core.get().getPlayerManager().getPlayer(player1);
                            LudosPlayer otherPlayer = ludosPlayer.getTeam().getPlayers().stream()
                                    .filter(p -> p != ludosPlayer)
                                    .findFirst()
                                    .orElse(null);

                            return String.format(
                                    "&f%s &7- &f%s",
                                    this.gameLogic.getPlayerKills(ludosPlayer),
                                    this.gameLogic.getPlayerKills(otherPlayer)
                            );
                        }
                )
        );
    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        gamePointsMaterials.put(Material.BEDROCK, "KILL_ZONE");
        return gamePointsMaterials;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        return ImmutableMap.<String, TeamRecord>builder()
                .put("PLAYERS", new TeamRecord(
                        "Joueurs",
                        1,
                        false,
                        true,
                        new ColorUtils.TeamColorSet(NamedTextColor.GRAY, DyeColor.WHITE, Color.WHITE),
                        (gameLogic::canPlayerRespawn),
                        null
                ))
                .putAll(ONLY_SPECTATOR.getTeamRecords())
                .build();
    }
}
