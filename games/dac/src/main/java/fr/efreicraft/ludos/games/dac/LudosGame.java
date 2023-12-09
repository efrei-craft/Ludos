package fr.efreicraft.ludos.games.dac;
import com.google.common.collect.ImmutableMap;
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
        name = "Dé à coudre",
        description = "Essayer de tomber dans l'eau et passez les trois rounds pour gagner !",
        authors = {"Nat.io"},
        color = "&9",
        mapFolder = "DAC",
        rules = @GameRules(
                maxPlayers = 8
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
        Core.get().getTeamManager().getTeam("PLAYERS").setFriendlyFire(false);
        gameLogic.onGameStart();
    }
    @Override
    public void postMapParse() {
        this.gameLogic.setBassinLocation(
                Core.get().getMapManager().getCurrentMap().getGamePoints().get("BASSIN").get(0).getLocation()
        );
        this.gameLogic.setPlateformeLocation(
                Core.get().getMapManager().getCurrentMap().getGamePoints().get("SPAWN_PLATEFORME").get(0).getLocation()
        );
        this.gameLogic.setPlateformeBoundaries(
                Core.get().getMapManager().getCurrentMap().getGamePoints().get("BORDER_PLATEFORME").get(0).getLocation(),
                Core.get().getMapManager().getCurrentMap().getGamePoints().get("BORDER_PLATEFORME").get(1).getLocation()
        );
        this.gameLogic.setSpawnPosition(
                Core.get().getMapManager().getCurrentMap().getSpawnPoints().get(Core.get().getTeamManager().getTeam("PLAYERS")).get(5).getLocation()
        );
    }
    @Override
    public void setupScoreboard(LudosPlayer player) {
        // No scoreboard for now
    }
    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        EnumMap<Material, String> gamePointsMaterials = new EnumMap<>(Material.class);
        gamePointsMaterials.put(Material.PRISMARINE_BRICKS, "BASSIN");
        gamePointsMaterials.put(Material.END_STONE_BRICKS, "BORDER_PLATEFORME");
        gamePointsMaterials.put(Material.CHISELED_STONE_BRICKS, "SPAWN_PLATEFORME");
        return gamePointsMaterials;
    }
    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        return ImmutableMap.<String, TeamRecord>builder()
                .put("PLAYERS", new TeamRecord(
                        "Joueurs",
                        1,
                        true,
                        true,
                        new ColorUtils.TeamColorSet(NamedTextColor.GRAY, DyeColor.WHITE, Color.GRAY),
                        null,
                        p -> {
                            p.entity().setHealth(20);
                            p.entity().setFoodLevel(20);
                            p.entity().setSaturation(20);
                            p.entity().setExp(0);
                            p.entity().setLevel(0);
                            p.entity().getInventory().clear();
                            p.entity().getInventory().setArmorContents(null);
                            p.entity().setGameMode(GameMode.ADVENTURE);
                        }
                ))
                .putAll(ONLY_SPECTATOR.getTeamRecords())
                .build();
    }
}
