package fr.efreicraft.ludos.games.sumo;

import com.google.common.collect.ImmutableMap;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.games.annotations.GameRules;
import fr.efreicraft.ludos.core.games.interfaces.Game;

import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

import static fr.efreicraft.ludos.core.teams.DefaultTeamRecordBuilder.DefaultTeamRecords.ONLY_SPECTATOR;

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
        this.gameLogic.setKillZoneLocation(
                Core.get().getMapManager().getCurrentMap().getGamePoints().get("KILL_ZONE").get(0).getLocation()
        );
    }

    @Override
    public void setupScoreboard(Player player) {
        // No scoreboard for now
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
                            ItemStack kbstick = new ItemStack(Material.STICK);
                            kbstick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1);
                            p.entity().getInventory().setItem(0, kbstick);
                        }
                ))
                .putAll(ONLY_SPECTATOR.getTeamRecords())
                .build();
    }
}
