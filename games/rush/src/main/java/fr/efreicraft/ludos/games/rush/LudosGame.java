package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.TeamRecord;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.EnumMap;
import java.util.Map;

// TODO: écrire une desc
@GameMetadata(
        name = "Rush",
        color = "&5",
        description = "",
        authors = {"Idir 'Niilyx' NAIT MEDDOUR"}
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
