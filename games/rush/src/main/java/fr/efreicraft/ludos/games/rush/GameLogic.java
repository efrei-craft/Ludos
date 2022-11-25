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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;

public class GameLogic {

    private World world;

    public final Map<Team, ArrayList<GamePoint>> TEAMS_ITEMSPAWNERS = new HashMap<>();
    public Map<Team, SpawnPoint> TEAMS_BED = new HashMap<>();

    private int yDeath;
    Merchant merchantBatisseur, merchantTerroriste, merchantTavernier, merchantArmurier;

    private BukkitTask stopWatchTask;

    /**
     * Le temps écoulé depuis le début de la partie. Techniquement, rien
     * n'arrête l'incrémentation de cette variable, sauf la fin de partie,
     * mais bon, on n'aura pas d'overflow après.
     */
    int time = 0;

    private final Set<Team> bedDestroyed = new HashSet<>(4);

    public void world(World world) {
        this.world = world;
    }

    public void setupVillagers() {
        for (GamePoint point : Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT_BATISSEUR")) {
            Location loc = point.getLocation().add(0.5, 1, 0.5);

            Entity villager = world.spawnEntity(loc, EntityType.VILLAGER);
            villager.setVelocity(new Vector(0, 1.4, 0));
            villager.setInvulnerable(true);
            villager.setSilent(true);
        }
    }

    public void setupBeds() {
        Location mid = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();
        for (SpawnPoint point : TEAMS_BED.values()) {

        }
    }

    public void setupMerchants() {
        Merchant batisseur = Bukkit.createMerchant(Component.text("Batisseur").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        batisseur.setRecipes(getTradesBatisseur());
        this.merchantBatisseur = batisseur;

        Merchant terroriste = Bukkit.createMerchant(Component.text("Terroriste").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        terroriste.setRecipes(getTradesTerroriste());
        this.merchantTerroriste = terroriste;

        Merchant tavernier = Bukkit.createMerchant(Component.text("Tavernier").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        tavernier.setRecipes(getTradesTavernier());
        this.merchantTavernier = tavernier;

        Merchant armurier = Bukkit.createMerchant(Component.text("Armurier").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD));
        batisseur.setRecipes(getTradesArmurier());
        this.merchantArmurier = armurier;
    }

    private List<MerchantRecipe> getTradesBatisseur() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.SANDSTONE, 2);
        MerchantRecipe sandstone = new MerchantRecipe(product, 0, 0, false, 0, 1, 1, 0, true);
        sandstone.addIngredient(new ItemStack(Material.BRICK));
        recipeList.add(sandstone);

        ItemStack product1 = new ItemStack(Material.END_STONE);
        MerchantRecipe endstone = new MerchantRecipe(product1, 0, 0, false, 0, 1, 1, 0, true);
        endstone.addIngredient(new ItemStack(Material.BRICK, 4));
        recipeList.add(endstone);

        ItemStack product2 = new ItemStack(Material.SOUL_SAND);
        MerchantRecipe soulsand = new MerchantRecipe(product2, 0, 0, false, 0, 1, 1, 0, true);
        soulsand.addIngredient(new ItemStack(Material.IRON_INGOT));
        recipeList.add(soulsand);

        ItemStack product3 = new ItemStack(Material.WOODEN_PICKAXE);
        product3.addEnchantment(Enchantment.DIG_SPEED, 1); // Dig_speed = Efficiency
        product3.addEnchantment(Enchantment.DURABILITY, 1);
        MerchantRecipe pick1 = new MerchantRecipe(product3, 0, 0, false, 0, 1, 1, 0, true);
        pick1.addIngredient(new ItemStack(Material.BRICK, 10));
        recipeList.add(pick1);

        ItemStack product4 = new ItemStack(Material.STONE_PICKAXE);
        product4.addEnchantment(Enchantment.DIG_SPEED, 2);
        product4.addEnchantment(Enchantment.DURABILITY, 1);
        MerchantRecipe pick2 = new MerchantRecipe(product4, 0, 0, false, 0, 1, 1, 0, true);
        pick2.addIngredient(new ItemStack(Material.IRON_INGOT, 5));
        recipeList.add(pick2);

        ItemStack product5 = new ItemStack(Material.IRON_PICKAXE);
        product5.addEnchantment(Enchantment.DIG_SPEED, 4);
        product5.addEnchantment(Enchantment.DURABILITY, 3);
        product5.addEnchantment(Enchantment.SILK_TOUCH, 1);
        MerchantRecipe pick3 = new MerchantRecipe(product5, 0, 0, false, 0, 1, 1, 0, true);
        pick3.addIngredient(new ItemStack(Material.IRON_INGOT, 10));
        pick3.addIngredient(new ItemStack(Material.GOLD_INGOT));
        recipeList.add(pick3);

        ItemStack product6 = new ItemStack(Material.LADDER);
        MerchantRecipe ladder = new MerchantRecipe(product6, 0, 0, false, 0, 1, 1, 0, true);
        ladder.addIngredient(new ItemStack(Material.BRICK, 5));
        recipeList.add(ladder);



        return recipeList;
    }

    private List<MerchantRecipe> getTradesTerroriste() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.GOLDEN_SWORD);
        product.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        product.addEnchantment(Enchantment.KNOCKBACK, 1);
        MerchantRecipe sword = new MerchantRecipe(product, 0, 0, false, 0, 1, 1, 0, true);
        sword.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        recipeList.add(sword);

        ItemStack product1 = new ItemStack(Material.GOLDEN_SWORD);
        product1.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        product1.addEnchantment(Enchantment.KNOCKBACK, 1);
        product1.addEnchantment(Enchantment.DURABILITY, 1);
        MerchantRecipe sword1 = new MerchantRecipe(product1, 0, 0, false, 0, 1, 1, 0, true);
        sword1.addIngredient(new ItemStack(Material.GOLD_INGOT, 2));
        recipeList.add(sword1);

        ItemStack product2 = new ItemStack(Material.DIAMOND_SWORD);
        product2.addEnchantment(Enchantment.DAMAGE_ALL, 4);
        product2.addEnchantment(Enchantment.KNOCKBACK, 2);
        MerchantRecipe sword2 = new MerchantRecipe(product2, 0, 0, false, 0, 1, 1, 0, true);
        sword2.addIngredient(new ItemStack(Material.EMERALD, 5));
        recipeList.add(sword2);

        ItemStack product3 = new ItemStack(Material.BOW);
        product3.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        MerchantRecipe bow = new MerchantRecipe(product3, 0, 0, false, 0, 1, 1, 0, true);
        bow.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        recipeList.add(bow);

        ItemStack product4 = new ItemStack(Material.BOW);
        product4.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        product4.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        MerchantRecipe bow1 = new MerchantRecipe(product4, 0, 0, false, 0, 1, 1, 0, true);
        bow1.addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
        recipeList.add(bow1);

        ItemStack product5 = new ItemStack(Material.BOW);
        product5.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        product5.addEnchantment(Enchantment.ARROW_DAMAGE, 4);
        MerchantRecipe bow2 = new MerchantRecipe(product5, 0, 0, false, 0, 1, 1, 0, true);
        bow2.addIngredient(new ItemStack(Material.EMERALD));
        bow2.addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
        recipeList.add(bow2);

        ItemStack product6 = new ItemStack(Material.ARROW);
        MerchantRecipe arrow = new MerchantRecipe(product6, 0, 0, false, 0, 1, 1, 0, true);
        arrow.addIngredient(new ItemStack(Material.GOLD_INGOT));
        recipeList.add(arrow);


        return recipeList;
    }

    private List<MerchantRecipe> getTradesTavernier() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.COOKED_BEEF);
        MerchantRecipe meat = new MerchantRecipe(product, 0, 0, false, 0, 1, 1, 0, true);
        meat.addIngredient(new ItemStack(Material.BRICK, 5));
        recipeList.add(meat);

        ItemStack product1 = new ItemStack(Material.GOLDEN_APPLE);
        MerchantRecipe gapple = new MerchantRecipe(product1, 0, 0, false, 0, 1, 1, 0, true);
        gapple.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        recipeList.add(gapple);

        ItemStack product2 = new ItemStack(Material.FISHING_ROD);
        MerchantRecipe fishingrod = new MerchantRecipe(product2, 0, 0, false, 0, 1, 1, 0, true);
        fishingrod.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        recipeList.add(fishingrod);

        ItemStack product3 = new ItemStack(Material.COBWEB);
        MerchantRecipe cobweb = new MerchantRecipe(product3, 0, 0, false, 0, 1, 1, 0, true);
        cobweb.addIngredient(new ItemStack(Material.BRICK, 6));
        cobweb.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        recipeList.add(cobweb);

        ItemStack product4 = new ItemStack(Material.TNT);
        MerchantRecipe bow1 = new MerchantRecipe(product4, 0, 0, false, 0, 1, 1, 0, true);
        bow1.addIngredient(new ItemStack(Material.IRON_INGOT, 6));
        recipeList.add(bow1);

        ItemStack product5 = new ItemStack(Material.FLINT_AND_STEEL);
        MerchantRecipe lighter = new MerchantRecipe(product5, 0, 0, false, 0, 1, 1, 0, true);
        lighter.addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
        recipeList.add(lighter);

        ItemStack product6 = new ItemStack(Material.REDSTONE_TORCH);
        MerchantRecipe redstonetorch = new MerchantRecipe(product6, 0, 0, false, 0, 1, 1, 0, true);
        redstonetorch.addIngredient(new ItemStack(Material.BRICK, 10));
        recipeList.add(redstonetorch);

        ItemStack product7 = new ItemStack(Material.ENDER_PEARL);
        MerchantRecipe enderpearl = new MerchantRecipe(product7, 0, 0, false, 0, 1, 1, 0, true);
        enderpearl.addIngredient(new ItemStack(Material.EMERALD));
        recipeList.add(enderpearl);

        return recipeList;
    }

    private List<MerchantRecipe> getTradesArmurier() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

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

            for (Team team : Core.get().getTeamManager().getTeams().values()) {
                if (time % 5 == 0){
                    rewardTeam(team, new ItemStack(Material.GOLD_INGOT));
                }
                rewardTeam(team, new ItemStack(Material.BRICK));
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
    }

    /**
     * Spawn stacks sur les spawners d'items de team, en les répartissant sur chaque point.
     * @param team La team récompensée
     * @param stacks Les stacks à distribuer
     */
    public void rewardTeam(Team team, ItemStack... stacks) {
        if (stacks == null) return;
        List<GamePoint> points = TEAMS_ITEMSPAWNERS.get(team);
        if (points == null) return;

        for (ItemStack stackTotal : stacks) {
            int distributed = stackTotal.getAmount();
            ItemStack stack = stackTotal.clone();
            stack.setAmount(1);

            while (true) {
                for (GamePoint gamePoint : points) {
                    Location loc = gamePoint.getLocation().add(0.5, 0, 0.5);
                    Item item = this.world.dropItem(loc, stack);
                    item.setVelocity(new Vector(0, 1, 0));
                    distributed--;

                    if (distributed == 0) return;
                }
            }

        }
    }

    /**
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

}
