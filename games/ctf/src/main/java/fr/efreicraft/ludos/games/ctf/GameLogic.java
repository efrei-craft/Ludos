package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.TeamWin;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
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

    private int killZoneY = -64;

    private Location redBaseLocation = null;
    private Location blueBaseLocation = null;

    //Le but de ces deux variables est de concerver la position des trapeaux dans le monde
    //afin de ne pas les confondre avec d'autres banières (posé par des builders par ex)
    private Location redFlagLocation = null;
    private Location blueFlagLocation = null;

    private int scoreTeamRed = 0;
    private int scoreTeamBlue = 0;
    private static final int SCORE_TO_WIN = 3;

    private static final double BASE_RADIUS = 4;


    public GameLogic(LudosGame ludosGame1) {
        ludosGame = ludosGame1;
    }

    /**
     * Faire apparaître les drapeaux sur la map, et stocker leurs positions
     * @param red_location position initiale du drapeau rouge
     * @param blue_location position initiale du drapeau bleu
     */
    public void initFlags(Location red_location, Location blue_location) {
        redBaseLocation = red_location;
        blueBaseLocation = blue_location;

        redFlagLocation = red_location;
        blueFlagLocation = blue_location;

        redBaseLocation.getWorld().setBlockData(redBaseLocation, Material.RED_BANNER.createBlockData());
        blueBaseLocation.getWorld().setBlockData(blueBaseLocation, Material.BLUE_BANNER.createBlockData());
    }


    /**
     * Permet de définir la position y de la killzone (tout joueur passant dessous sera tué)
     * @param kill_zone_y position y de la killzone
     */
    public void initKillZone(int kill_zone_y) {
        killZoneY = kill_zone_y;
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
        Material flagMaterial = getFlagMaterial(teamOfFlag);
        if(flagMaterial == null) return true;

        //Vérifier si le joueur n'essaie pas de casser un drapeau du décor
        if(!flagLocation.equals(getFlagLocation(flagMaterial))) return true;

        //Vérifier si le joueur essaie de casser le drapeau de sa propre équipe (si oui le replacer à sa position initiale)
        if(Objects.equals(player.getTeam().getName(), teamOfFlag)) {
            //Obtenir la position de la base du drapeau et vérifier si c'est le bloc qu'essaie de casser le joueur
            Location baseLocation = getBaseLocation(teamOfFlag);
            if(flagLocation.equals(baseLocation)) return true;    //si oui annuler le cassage

            //Sinon le replacer à sa position initiale
            baseLocation.getWorld().setBlockData(baseLocation, flagMaterial.createBlockData());
            MessageUtils.broadcastMessage(
                    MessageUtils.ChatPrefix.GAME,
                    getTeamColorCode(teamOfFlag) + "Le drapeau " + teamOfFlag.toLowerCase()
                            + " a été renvoyé à sa base."
            );

            updateFlagLocation(flagMaterial, baseLocation);

            return false;   //Détruire le bloc
        }

        //Le joueur casse le drapeau de la team adverse
        MessageUtils.broadcastMessage(
                MessageUtils.ChatPrefix.GAME,
                getTeamColorCode(player.getTeam().getName()) + player.getName()
                        + " &ra capturé le drapeau de l'équipe "
                        + getTeamColorCode(teamOfFlag) + teamOfFlag.toLowerCase()
        );

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
        Location dropLocation = null;
        if(dropOnPlayerPosition) {  //drop sur le joueur
            Location playerLocation = player.entity().getLocation();

            //On souhaite drop le drapeau sur le premier block solide en dessous du joueur
            for(int y = playerLocation.getBlockY(); y > killZoneY; y--) {
                //Vérifier le type du block
                Block belowBock = world.getBlockAt(playerLocation.getBlockX(), y, playerLocation.getBlockZ());
                Material belowBockMaterial = belowBock.getBlockData().getMaterial();
                if(belowBockMaterial.isSolid()) {   //block solide
                    dropLocation = new Location(world,
                            playerLocation.getBlockX(), y+1, playerLocation.getBlockZ());
                    break;
                }
            }

            //Si on est en dessous de la killzone, replacer le drapeau à sa base
            if(dropLocation == null) {
                dropLocation = getBaseLocation(helmetSlotMaterial);
                if(dropLocation == null) return;
            }
        }
        else {  //replacer à la base
            dropLocation = getBaseLocation(helmetSlotMaterial);
            if(dropLocation == null) return;
        }

        //Replacer le drapeau sur la map
        dropLocation.getWorld().setBlockData(dropLocation, helmetSlotMaterial.createBlockData());
        updateFlagLocation(helmetSlotMaterial, dropLocation);

        //TODO : si le joueur meurt au dessus du vide (sans qu'il y ait de sol en dessous de lui) il faut replacer le drapeau à son spawn
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

    /**
     * Vérifier si le joueur est sous la killzone. Si oui le tuer.
     * @param player joueur à vérifier
     */
    public void checkKillZone(LudosPlayer player) {
        if(player.entity().getLocation().getBlockY() <= killZoneY && player.entity().getGameMode() == GameMode.SURVIVAL) {
            dropFlagIfCarried(player, false);   //remettre le drapeau à sa base
            player.entity().setHealth(0);   //tuer le joueur
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


    public void spawnParticleDebug() {
        Particle.DustOptions dustOptionsBases = new Particle.DustOptions(Color.GREEN, 1.0f);
        world.spawnParticle(Particle.REDSTONE, redBaseLocation, 5, dustOptionsBases);
        world.spawnParticle(Particle.REDSTONE, blueBaseLocation, 5, dustOptionsBases);

        Particle.DustOptions dustOptionsFlags = new Particle.DustOptions(Color.ORANGE, 1.0f);
        world.spawnParticle(Particle.REDSTONE, redFlagLocation, 5, dustOptionsFlags);
        world.spawnParticle(Particle.REDSTONE, blueFlagLocation, 5, dustOptionsFlags);
    }

    public void spawnParticles(int time) {
        spawnParticleAroundBase("RED");
        spawnParticleAroundBase("BLUE");

        //spawnParticleDebug();
    }


    //TODO : trouver un moyen de mieux gérer les trucs qu'il y a ci-dessous

    /**
     * Obtenir la position d'une base (drapeau) à partir d'un nom de team
     * @param teamName nom de la team
     * @return position de la base de la team (ou null si la team n'existe pas)
     */
    public Location getBaseLocation(String teamName) {
        switch (teamName) {
            case "Rouge" -> { return redBaseLocation; }
            case "Bleu" -> { return blueBaseLocation; }
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
            case RED_BANNER -> { return redBaseLocation; }
            case BLUE_BANNER -> { return blueBaseLocation; }
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


    /**
     * Mettre à jour la position d'un des drapeaux
     * @param flagMaterial Matériel correspondant au drapeau
     * @param location Nouvelle position du drapeau dans le monde
     */
    public void updateFlagLocation(Material flagMaterial, Location location) {
        //"arrondir" la position afin de pouvoir la comparer
        Location l = new Location(world, location.getBlockX(), location.getBlockY(), location.getBlockZ());
        switch (flagMaterial) {
            case RED_BANNER -> { redFlagLocation = l; }
            case BLUE_BANNER -> { blueFlagLocation = l; }
            default -> { /* rien */ }
        }
    }

    /**
     * Obtenir la position d'un drapeau dans le monde (si le drapeau est dans un inventaire, sa position précédente dans le monde est retourné)
     * @param flagMaterial Matériel correspondant au drapeau
     * @return la position du drapeau dans le monde
     */
    public Location getFlagLocation(Material flagMaterial) {
        switch (flagMaterial) {
            case RED_BANNER -> { return redFlagLocation; }
            case BLUE_BANNER -> { return blueFlagLocation; }
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
                    ludosGame.setWinnerAndEndGame(new TeamWin(Core.get().getTeamManager().getTeam("RED")));
            }
            case "Bleu" -> {
                scoreTeamBlue += 1;
                if(scoreTeamBlue >= SCORE_TO_WIN)
                    ludosGame.setWinnerAndEndGame(new TeamWin(Core.get().getTeamManager().getTeam("BLUE")));
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
