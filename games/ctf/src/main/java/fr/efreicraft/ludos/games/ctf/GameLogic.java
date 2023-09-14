package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;

public class GameLogic {
    private LudosGame ludosGame;

    private Location redLocation = null;
    private Location blueLocation = null;

    private int scoreTeamRed = 0;
    private int scoreTeamBlue = 0;
    private static final int SCORE_TO_WIN = 3;


    public GameLogic(LudosGame ludosGame1) {
        ludosGame = ludosGame1;
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

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta lam = (LeatherArmorMeta)chestplate.getItemMeta();
        lam.setColor(player.getTeam().getColor().bukkitColor());
        chestplate.setItemMeta(lam);
        player.entity().getInventory().setChestplate(chestplate);
        //TODO : ajouter + d'équipement ici ?
    }


    /**
     * Vérifier si le joueur peut casser le drapeau, et si oui le casser.
     * @param player joueur qui a cassé le drapeau
     * @param teamOfFlag la team du drapeau qui a été cassé (utilisé pour empécher une équipe de détruire son propre drapeau)
     * @return true pour annuler l'event du cassage, sinon retourne false
     */
    public boolean canBreakFlag(LudosPlayer player, String teamOfFlag) {
        //Vérifier si le joueur n'essaie pas de casser le drapeau de sa propre équipe
        if(Objects.equals(player.getTeam().getName(), teamOfFlag)) {
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

        Material flagMaterial = getFlagMaterial(teamOfFlag);
        if(flagMaterial == null) return true;

        //Le drapeau est placé sur la tête du joueur qui le récupère afin d'être visible par tout le monde,
        //et dans le deuxième slot de la hotbar afin d'être visible par le joueur lui-même
        player.entity().getInventory().setHelmet(new ItemStack(flagMaterial));
        player.entity().getInventory().setItem(1, new ItemStack(flagMaterial));    //1 = deuxième slot hotbar

        //ajouter effet de glow
        player.entity().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 423310, 1));

        return false;
    }


    /**
     * Si le joueur possède un drapeau, il sera retiré de l'inventaire et replacé à sa position initiale
     * @param player le joueur dont on souhaite retirer le drapeau
     */
    public void dropFlagIfCarried(LudosPlayer player) {
        ItemStack helmetItem = player.entity().getInventory().getHelmet();
        if(helmetItem == null) return;      //Le joueur ne possède aucun drapeau dans ce cas

        Material helmetSlotMaterial = helmetItem.getType();
        player.entity().getInventory().clear(39);   //39 = slot helmet
        player.entity().getInventory().clear(1);    // 1 = deuxième slot hotbar

        //Déterminer quel drapeau replacer sur la map
        Location baseLocation = getBaseLocation(helmetSlotMaterial);
        if(baseLocation == null) return;

        //Replacer le drapeau
        baseLocation.getWorld().setBlockData(baseLocation, helmetSlotMaterial.createBlockData());

        //Retirer l'effet de glow
        player.entity().removePotionEffect(PotionEffectType.GLOWING);
    }


    /**
     * Appelé lorque l'on souhaite savoir si un joueur peut marquer un point
     * @param player joueur qui essaie de marquer un point
     * @param locationToCompare position à laquelle on souhaite calculer la distance avec la base du joueur
     */
    public void tryToScore(LudosPlayer player, Location locationToCompare) {
        ItemStack helmetItem = player.entity().getInventory().getHelmet();
        if(helmetItem == null) return;      //Le joueur ne possède aucun drapeau dans ce cas

        Location baseLocation = getBaseLocation(player.getTeam().getName());      //position de la base du joueur player
        if(baseLocation == null) return;

        //Vecteur de la distance entre le drapeau qui vient d'être posé et la base du joueur player
        Vector vec = new Vector(
                locationToCompare.getX() - baseLocation.getX(),
                locationToCompare.getY() - baseLocation.getY(),
                locationToCompare.getZ() - baseLocation.getZ()
        );

        //Norme du vecteur
        double distance = vec.length();
        //player.sendMessage(MessageUtils.ChatPrefix.GAME, distance+"");

        //Si la distance est inférieure à 4, alors le joueur marque 1 point pour son équipe
        if(distance < 4) {
            player.sendMessage(MessageUtils.ChatPrefix.GAME, "Tu as marqué un point !");
            dropFlagIfCarried(player);
            incrementScore(player.getTeam().getName());
        }
        // Ancien code
        //else {
        //    player.sendMessage(MessageUtils.ChatPrefix.GAME, "Tu dois ramener le drapeau à ta base.");
        //}
    }


    /**
     * Obtenir la position d'une base (drapeau) à partir d'un nom de team
     * @param teamName nom de la team
     * @return position de la base de la team (ou null si la team n'existe pas)
     */
    public Location getBaseLocation(String teamName) {
        switch (teamName) {
            case "Red" -> { return redLocation; }
            case "Blue" -> { return blueLocation; }
            default -> { return null; }
        }
    }

    /**
     * Obtenir la position d'une base (drapeau) à partir d'un matériel
     * @param material matériel correspondant au drapeau de la team
     * @return position de la base de la team (ou null si la team n'existe pas)
     */
    public Location getBaseLocation(Material material) {
        switch (material) {
            case RED_BANNER -> { return redLocation; }
            case BLUE_BANNER -> { return blueLocation; }
            default -> { return null; }
        }
    }

    public Material getFlagMaterial(String teamName) {
        switch(teamName) {
            case "Red" -> { return Material.RED_BANNER; }
            case "Blue" -> { return Material.BLUE_BANNER; }
            default -> { return null; }
        }
    }

    /**
     * Incrémenter le score d'une team
     * @param teamName nom de la team
     */
    public void incrementScore(String teamName) {
        switch (teamName) {
            case "Red" -> {
                scoreTeamRed += 1;
                if(scoreTeamRed >= SCORE_TO_WIN)
                    ludosGame.setWinnerAndEndGame(new CtfWinners("RED", Color.RED));
            }
            case "Blue" -> {
                scoreTeamBlue += 1;
                if(scoreTeamBlue >= SCORE_TO_WIN)
                    ludosGame.setWinnerAndEndGame(new CtfWinners("BLUE", Color.BLUE));
            }
        }
    }

    /**
     * Obtenir le score d'une team
     * @param teamName nom de la team
     * @return le score de la team en question (ou -1 si la team n'existe pas)
     */
    public int getScore(String teamName) {
        switch (teamName) {
            case "Red" -> { return scoreTeamRed; }
            case "Blue" -> { return scoreTeamBlue; }
            default -> { return -1; }
        }
    }
}
