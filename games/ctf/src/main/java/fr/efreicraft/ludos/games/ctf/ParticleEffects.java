package fr.efreicraft.ludos.games.ctf;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.*;

/**
 * Classe pour gérer les particules du CTF
 */
public class ParticleEffects {
    private GameLogic game;
    protected boolean enabled = false;
    private static final double BASE_RADIUS = 4;    //TODO : partager cette constante entre GameLogic et ParticleEffects

    public ParticleEffects(GameLogic game1) {
        game = game1;
    }

    public void startParticles() {
        if(enabled) return;
        enabled = true;
        GameTimer gt = new GameTimer(this::spawnParticles, -1, 10);
    }

    //TODO : ajouter une méthode stopParticle ?


    public void spawnParticleAroundBase(String teamKey) {
        Team team = Core.get().getTeamManager().getTeam(teamKey);
        if(team.getPlayers().isEmpty()) return;

        World world = game.getWorld();
        Location baseLocation = game.getBaseLocation(team.getName());
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
        World world = game.getWorld();

        Location redBaseLocation = game.getBaseLocation("Rouge");
        Location blueBaseLocation = game.getBaseLocation("Bleu");
        Location redFlagLocation = game.getFlagLocation(Material.RED_BANNER);
        Location blueFlagLocation = game.getFlagLocation(Material.BLUE_BANNER);

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
}
