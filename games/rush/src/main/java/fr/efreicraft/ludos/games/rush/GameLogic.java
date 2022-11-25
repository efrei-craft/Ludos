package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.lang.reflect.Array;
import java.util.*;

public class GameLogic {

    private World world;

    public final Map<Team, ArrayList<GamePoint>> TEAMS = new HashMap<>();

    private int yDeath;
    Merchant merchant;

    private BukkitTask stopWatchTask;

    /**
     * Le temps écoulé depuis le début de la partie. Techniquement, rien
     * n'arrête l'incrémentation de cette variable, sauf la fin de partie,
     * mais bon, on n'aura pas d'overflow après.
     */
    int time = 0;

    private final Set<Team> bedDestroyed = new HashSet<>(4);

    public GameLogic() {
    }

    public void world(World world) {
        this.world = world;
        setMerchant();
    }

    private void setMerchant() {
    public void setupVillagers() {
        for (GamePoint point : Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT")) {
            Location loc = point.getLocation().add(0.5, 1, 0.5);

            Entity villager = world.spawnEntity(loc, EntityType.VILLAGER);
            villager.setVelocity(new Vector(0, 1.4, 0));
            villager.setInvulnerable(true);
            villager.setSilent(true);
        }
    }

    public void setMerchant() {
        Merchant merchant = Bukkit.createMerchant(Component.text("Boutique").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        merchant.setRecipes(getTrades());

        this.merchant = merchant;
    }

    private List<MerchantRecipe> getTrades() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        MerchantRecipe blocs = new MerchantRecipe(new ItemStack(Material.SANDSTONE, 4), 0, 0, false, 0, 1, 0, 0, true);
        blocs.addIngredient(new ItemStack(Material.BRICK));
        recipeList.add(blocs);

        MerchantRecipe sword1 = new MerchantRecipe(new ItemStack(Material.STONE_SWORD), 0, 0, false, 0, 1, 0, 0, true);
        sword1.addIngredient(new ItemStack(Material.IRON_INGOT, 4));
        recipeList.add(sword1);

        MerchantRecipe pick1 = new MerchantRecipe(new ItemStack(Material.STONE_PICKAXE), 0, 0, false, 0, 1, 0, 0, true);
        pick1.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        recipeList.add(pick1);

        return recipeList;
    }

    public void yDeath(int yDeath) {
        this.yDeath = yDeath;
    }
    public int yDeath() {
        return this.yDeath;
    }

    public void startStopwatch() {
        this.stopWatchTask = Bukkit.getScheduler().runTaskTimer(Core.get().getGameManager().getCurrentPlugin(), () -> {
            if (time % 5 == 0) {

            }


            time++;
        }, 0, 20);
    }

    public void stopStopwatch() {
        if (this.stopWatchTask != null)
            this.stopWatchTask.cancel();
    }

    /**
     * Donne les récompenses à la team du joueur ayant donné le coup de grâce au mort
     * @param killer Le tueur
     */
    public void handleFinishOffByPlayer(Player killer) {
        fr.efreicraft.ludos.core.players.Player gamer = Utils.getLudosPlayer(killer);
        if (gamer == null) return;

        rewardTeam(gamer.getTeam());
    }

    public void rewardTeam(Team team, ItemStack... stacks) {

    }

    //TODO
    /**
     * Vérifie si le lit de *team* a été détruit.
     * Vérifie si le lit de team a été détruit.
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

    public void getTeamItemSpawner() {

    }

}
