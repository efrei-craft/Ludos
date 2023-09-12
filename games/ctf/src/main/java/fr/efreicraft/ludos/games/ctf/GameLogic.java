package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.players.LudosPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GameLogic {
    public Location redLocation;
    public Location blueLocation;

    public int scoreTeamRed;
    public int scoreTeamBlue;

    public GameLogic() {
        redLocation = null;
        blueLocation = null;

        scoreTeamRed = 0;
        scoreTeamBlue = 0;
    }

    //Faire apparaître les drapeaux sur la map, et stocker leurs positions
    public void initFlags(Location red_location, Location blue_location) {
        redLocation = red_location;
        blueLocation = blue_location;

        redLocation.getWorld().setBlockData(redLocation, Material.RED_BANNER.createBlockData());
        blueLocation.getWorld().setBlockData(blueLocation, Material.BLUE_BANNER.createBlockData());
    }

    public void preparePlayerToSpawn(LudosPlayer player) {
        player.entity().setGameMode(GameMode.SURVIVAL);
        player.entity().getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
        //TODO : ajouter + d'équipement ici ?
    }
}
