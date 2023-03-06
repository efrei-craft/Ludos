package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;

import javax.annotation.Nullable;

import static org.bukkit.block.BlockFace.*;

public class Utils {

    @Nullable
    public static LudosPlayer getLudosPlayer(org.bukkit.entity.Player player) {
        return Core.get().getPlayerManager().getPlayer(player);
    }

    public static fr.efreicraft.ludos.core.teams.Team getTeam(String team) {
        return Core.get().getTeamManager().getTeam(team);
    }

    /**
     * ...
     * @param reference Le point de référence selon lequel mesurer
     * @param self Le point sujet à la mesure
     * @return Une des <b>quatres</b> directions cardinales OU {@link BlockFace#SELF}
     */
    //TODO tester ça là
    public static BlockFace whereAmIRelatedTo(Location reference, Location self) {
        int x = reference.getBlockX() - self.getBlockX();
        int z = reference.getBlockZ() - self.getBlockZ();

        if (x == 0) {
            if (z > 0)
                return SOUTH;
            else if (z < 0)
                return NORTH;
            else
                return SELF;
        } else {
            if (x > 0)
                return EAST;
            else
                return WEST;
        }
    }
}
