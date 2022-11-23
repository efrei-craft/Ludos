package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.Set;

public class GameLogic {

    private int yDeath;
    final Villager merchant;

    private BukkitTask stopWatchTask;
    int time;

    private Set<Team> bedDestroyed = new HashSet<>(4);

    public GameLogic() {
        merchant = null;
    }

    public void yDeath(int yDeath) {
        this.yDeath = yDeath;
    }

    public void startStopwatch() {
        this.stopWatchTask = Bukkit.getScheduler().runTaskTimer(Core.get().getGameManager().getCurrentPlugin(), () -> {
            time++;
        }, 0, 20);
    }

    /**
     * Donne les récompenses à la team du joueur ayant donné le coup de grâce au mort
     * @param killer Le tueur
     */
    public void handleFinishOffByPlayer(Player killer) {

    }

    //TODO
    /**
     * Vérifie si le lit de *team* a été détruit.
     * @param team L'équipe à vérifier
     * @return {@code true} si le lit de team est détruit, sinon {@code false}.
     */
    public boolean bedDestroyed(Team team) {
        return bedDestroyed.contains(team);
    }

    /**
     * Déclare le lit de *team* comme étant détruit.
     * @param team L'équipe qui a perdu son lit
     */
    public void bedDestroyed(Team team, boolean destroyed) {
        if (destroyed)
            bedDestroyed.add(team);
        else
            bedDestroyed.remove(team);
    }

}
