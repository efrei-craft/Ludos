package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Objects;

/**
 * Classe principale de la logique du leu CTF
 * @author Ewenn BAUDET
 */
public class GameLogic {
    private LudosGame ludosGame;
    private World world;

    private Location redLocation = null;
    private Location blueLocation = null;

    private int scoreTeamRed = 0;
    private int scoreTeamBlue = 0;
    private static final int SCORE_TO_WIN = 3;

    private static final double BASE_RADIUS = 4;


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
     * @param teamOfFlag la team du drapeau qui a été cassé
     * @param flagLocation la position du drapeau qui a été cassé
     * @return true pour annuler l'event du cassage, sinon retourne false
     */
    public boolean handleBreakFlag(LudosPlayer player, String teamOfFlag, Location flagLocation) {
        //Vérifier si le joueur essaie de casser le drapeau de sa propre équipe (si oui le replacer à sa position initiale)
        if(Objects.equals(player.getTeam().getName(), teamOfFlag)) {
            //Obtenir la position de la base du drapeau et vérifier si c'est le bloc qu'essaie de casser le joueur
            Location baseLocation = getBaseLocation(teamOfFlag);
            if(flagLocation.equals(baseLocation)) return true;    //si oui annuler le cassage

            //Sinon le replacer à sa position initiale
            baseLocation.getWorld().setBlockData(baseLocation, getFlagMaterial(teamOfFlag).createBlockData());
            MessageUtils.broadcastMessage(
                    MessageUtils.ChatPrefix.GAME,
                    getTeamColorCode(teamOfFlag) + "Le drapeau " + teamOfFlag.toLowerCase()
                            + " a été renvoyé à sa base."
            );

            return false;   //Détruire le bloc
        }

        //Le joueur casse le drapeau de la team adverse
        MessageUtils.broadcastMessage(
                MessageUtils.ChatPrefix.GAME,
                getTeamColorCode(player.getTeam().getName()) + player.getName()
                        + " &ra capturé le drapeau de l'équipe "
                        + getTeamColorCode(teamOfFlag) + teamOfFlag.toLowerCase()
        );

        Material flagMaterial = getFlagMaterial(teamOfFlag);
        if(flagMaterial == null) return true;

        //Le drapeau est placé sur la tête du joueur qui le récupère afin d'être visible par tout le monde,
        //et dans le deuxième slot de la hotbar afin d'être visible par le joueur lui-même
        player.entity().getInventory().setHelmet(new ItemStack(flagMaterial));
        player.entity().getInventory().setItem(1, new ItemStack(flagMaterial));    //1 = deuxième slot hotbar

        //ajouter effet de glow
        player.entity().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 423310, 1));

        return false;   //détruire le bloc
    }


    /**
     * Si le joueur possède un drapeau, il sera retiré de l'inventaire et replacé sur la map
     * @param player le joueur dont on souhaite retirer le drapeau
     * @param dropOnPlayerPosition true : drop le drapeau à la position du joueur | false : tp le drapeau à sa base
     */
    public void dropFlagIfCarried(LudosPlayer player, boolean dropOnPlayerPosition) {
        ItemStack helmetItem = player.entity().getInventory().getHelmet();
        if(helmetItem == null) return;      //Le joueur ne possède aucun drapeau dans ce cas

        //Retirer l'effet de glow
        player.entity().removePotionEffect(PotionEffectType.GLOWING);

        Material helmetSlotMaterial = helmetItem.getType();
        player.entity().getInventory().clear(39);   //39 = slot helmet
        player.entity().getInventory().clear(1);    // 1 = deuxième slot hotbar

        //Déterminer où replacer le drapeau sur la map
        Location dropLocation;
        if(dropOnPlayerPosition) {
            dropLocation = player.entity().getLocation();
        }
        else {
            dropLocation = getBaseLocation(helmetSlotMaterial);
            if(dropLocation == null) return;
        }

        //Replacer le drapeau sur la map
        dropLocation.getWorld().setBlockData(dropLocation, helmetSlotMaterial.createBlockData());
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

        //Si la distance est inférieure à une certaine distance, alors le joueur marque 1 point pour son équipe
        if(distance < BASE_RADIUS) {
            String playerTeam = player.getTeam().getName();
            MessageUtils.broadcastMessage(
                    MessageUtils.ChatPrefix.GAME,
                    getTeamColorCode(playerTeam) + player.getName() + " a marqué un point pour l'équipe "
                            + playerTeam.toLowerCase()
            );
            dropFlagIfCarried(player, false);   //Refaire spawn le drapeau à la base adverse
            incrementScore(player.getTeam().getName());
        }
    }

    public void spawnParticleAroundBase(String teamKey) {
        Team team = Core.get().getTeamManager().getTeam(teamKey);
        if(team.getPlayers().isEmpty()) return;

        Location baseLocation = getBaseLocation(team.getName());
        Location centerLocation = new Location(world, baseLocation.getX()+0.5, baseLocation.getY(), baseLocation.getZ()+0.5);
        Color teamColor = team.getColor().bukkitColor();

        Particle.DustOptions dustOptions = new Particle.DustOptions(teamColor, 1.0f);

        double circleStep = Math.PI/30;
        for(double angle = 0.0; angle < Math.PI*2; angle += circleStep) {
            world.spawnParticle(Particle.REDSTONE,
                    centerLocation.getX() + Math.cos(angle)*BASE_RADIUS,
                    centerLocation.getY(),
                    centerLocation.getZ() + Math.sin(angle)*BASE_RADIUS,
                    5, dustOptions);
        }
    }

    public void spawnParticles(int time) {
        spawnParticleAroundBase("RED");
        spawnParticleAroundBase("BLUE");
    }


    //TODO : trouver un moyen de mieux gérer les trucs qu'il y a ci-dessous

    /**
     * Obtenir la position d'une base (drapeau) à partir d'un nom de team
     * @param teamName nom de la team
     * @return position de la base de la team (ou null si la team n'existe pas)
     */
    public Location getBaseLocation(String teamName) {
        switch (teamName) {
            case "Rouge" -> { return redLocation; }
            case "Bleu" -> { return blueLocation; }
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
            case "Rouge" -> { return Material.RED_BANNER; }
            case "Bleu" -> { return Material.BLUE_BANNER; }
            default -> { return null; }
        }
    }

    public String getTeamColorCode(String teamName) {
        switch (teamName) {
            case "Rouge" -> { return "&c"; }
            case "Bleu" -> { return "&9"; }
            default -> {return ""; }
        }
    }

    /**
     * Incrémenter le score d'une team
     * @param teamName nom de la team
     */
    public void incrementScore(String teamName) {
        switch (teamName) {
            case "Rouge" -> {
                scoreTeamRed += 1;
                if(scoreTeamRed >= SCORE_TO_WIN)
                    ludosGame.setWinnerAndEndGame(new CtfWinners("RED"));
            }
            case "Bleu" -> {
                scoreTeamBlue += 1;
                if(scoreTeamBlue >= SCORE_TO_WIN)
                    ludosGame.setWinnerAndEndGame(new CtfWinners("BLUE"));
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
            case "Rouge" -> { return scoreTeamRed; }
            case "Bleu" -> { return scoreTeamBlue; }
            default -> { return -1; }
        }
    }

    public void setWorld(World world1) {
        world = world1;
    }
}
