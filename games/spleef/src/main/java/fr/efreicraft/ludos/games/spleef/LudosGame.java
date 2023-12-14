package fr.efreicraft.ludos.games.spleef;

import fr.efreicraft.ecatup.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.Map;

/**
 * Spleef game entrypoint
 * @author Idir NM. {@literal <idir.nait-meddour@efrei.net>}
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Minigames/Spleef
 */

@GameMetadata(
        name = "Spleef",
        color = "&e",
        description = "Détruisez le sol et éliminez vos adversaires !",
        authors = {"Niilyx"},
        rules = @GameRules(
                minPlayers = 2,
                minPlayersToStart = 2, // vraiment ?
                maxPlayers = 24
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
        this.setEventListener(new EventListener(this.gameLogic));

        this.gameLogic.setupTheShovel();
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
        Core.get().getMapManager().getCurrentMap().getWorld().setTime(6);
    }

    @Override
    public void beginGame() {
        super.beginGame();

        this.gameLogic.giveTheInventory();
        this.gameLogic.setupPlayers();
        this.gameLogic.startTimer();
    }

    @Override
    public boolean checkIfGameHasToBeEnded() {
        return super.checkIfGameHasToBeEnded();
    }

    @Override
    public void setupScoreboard(LudosPlayer player) {
        player.getBoard().clearFields();

        if (!gameLogic.suddenDeath) {
            player.getBoard().setField(0,
                    new ScoreboardField(
                            "&4Mort subite dans",
                            false,
                            player1 -> {
                                int minutes = this.gameLogic.time / 60;
                                int seconds = this.gameLogic.time % 60;
                                return String.format("   %2d:%02d", minutes, seconds);
                            }
                    )
            );
        } else {
            player.getBoard().setField(0,
                    new ScoreboardField(
                            "&4Mort subite !",
                            false,
                            player1 -> (gameLogic.redTextFlasher ? "&c" : "&r") + "   00:00"
                    )
            );
        }
        player.getBoard().setField(
                1,
                new ScoreboardField(
                        "&eJoueurs restants :",
                        true,
                        player1 -> String.format("   %2d", Core.get().getPlayerManager().getPlayingPlayers().size())
                )
        );
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
