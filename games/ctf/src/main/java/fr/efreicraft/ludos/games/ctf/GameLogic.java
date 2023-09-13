package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class GameLogic {
    private Location redLocation = null;
    private Location blueLocation = null;

    private int scoreTeamRed = 0;
    private int scoreTeamBlue = 0;

    public GameLogic() {

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


    //player : joueur qui a cassé le drapeau
    //teamToCheck : la team du drapeau qui a été cassé (utilisé pour empécher une équipe de détruire son propre drapeau)
    //retourne true pour annuler l'event du cassage, sinon retourne false
    public boolean BreakFlag(LudosPlayer player, String teamToCheck) {
        //Vérifier si le joueur n'essaie pas de casser le drapeau de sa propre équipe
        if(Core.get().getTeamManager().getTeam(teamToCheck).getPlayers().contains(player)) {
            player.sendMessage(
                    MessageUtils.ChatPrefix.GAME,
                    "Tu ne peux pas récupérer ton propre drapeau !"
            );
            return true;
        }

        player.sendMessage(
                MessageUtils.ChatPrefix.GAME,
                "Tu as récupéré le drapeau adverse !"
        );

        //TODO : stocker les joueurs possédant les drapeaux dans la classe

        return false;
    }
}
