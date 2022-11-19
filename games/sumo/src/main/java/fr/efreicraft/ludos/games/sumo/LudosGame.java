package fr.efreicraft.ludos.games.sumo;

import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.interfaces.GameMetadata;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.Map;

@GameMetadata(
        name = "Sumo",
        authors = {"DocSystem"},
        description = "Poussez votre adversaire en dehors du ring !",
        color = "&c",
        version = "1.0",
        minPlayers = 1
)
public class LudosGame extends Game {
    @Override
    public void preMapParse(World world) {

    }

    @Override
    public void postMapParse() {

    }

    @Override
    public void setupScoreboard(Player player) {

    }

    @Override
    public EnumMap<Material, String> getGamePointsMaterials() {
        return null;
    }

    @Override
    public Map<String, TeamRecord> getTeamRecords() {
        return null;
    }
}
