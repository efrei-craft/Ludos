package fr.efreicraft.ludos.games.rush;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.maps.points.GamePoint;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.teams.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class GameLogic {

    private World world;

    /**
     * Calculé par rapport au milieu de la carte.
     */
    public static final int MAX_BUILD_HEIGHT = 30;

    /**
     * Rayon autour duquel le joueur ne peut pas poser de TNT, par rapport à son point de spawn.
     */
    public static final int NO_TNT_RADIUS = 16;

    public final Map<Team, ArrayList<GamePoint>> TEAMS_ITEMSPAWNERS = new HashMap<>();
    public GamePoint[] TEAMS_BED = new GamePoint[4];

    private int yDeath;
    Merchant merchantBatisseur, merchantTerroriste, merchantTavernier, merchantArmurier;

    private final Set<Team> bedDestroyed = new HashSet<>(4);

    public void world(World world) {
        this.world = world;
    }

    public void preparePlayerToSpawn(LudosPlayer player) {
        player.entity().setGameMode(GameMode.SURVIVAL);
        player.entity().getActivePotionEffects().forEach(effect -> player.entity().removePotionEffect(effect.getType()));

        player.entity().getInventory().clear();
        ItemStack sword = new ItemStack(Material.STONE_SWORD);
        ItemStack pickaxe = new ItemStack(Material.WOODEN_PICKAXE);
        pickaxe.addEnchantment(Enchantment.DURABILITY, 1);

        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.LEATHER_BOOTS);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[3] = new ItemStack(Material.LEATHER_HELMET);

        for (ItemStack item : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(player.getTeam().getColor().bukkitColor());
            meta.addEnchant(Enchantment.DURABILITY, 1, false);
            item.setItemMeta(meta);
        }

        player.entity().getInventory().setItem(0, sword);
        player.entity().getInventory().setItem(1, pickaxe);
        player.entity().getInventory().setArmorContents(armor);
    }

    public void setupVillagers() {
        for (GamePoint point : Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT_BATISSEUR")) {
            Location loc = point.getLocation().add(0.5, 0, 0.5);
            makeVillager(loc, "Bâtisseur");
        }
        for (GamePoint point : Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT_TERRORISTE")) {
            Location loc = point.getLocation().add(0.5, 0, 0.5);
            makeVillager(loc, "Terroriste");
        }
        for (GamePoint point : Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT_TAVERNIER")) {
            Location loc = point.getLocation().add(0.5, 0, 0.5);
            makeVillager(loc, "Tavernier");
        }
        for (GamePoint point : Core.get().getMapManager().getCurrentMap().getGamePoints().get("MERCHANT_ARMURIER")) {
            Location loc = point.getLocation().add(0.5, 0, 0.5);
            makeVillager(loc, "Armurier");
        }
    }

    public void makeVillager(Location loc, String customName) {
        Villager villager = (Villager) world.spawnEntity(loc, EntityType.VILLAGER);
        villager.customName(Component.text(customName));
        villager.setInvulnerable(true);
        villager.setSilent(true);
        villager.clearReputations();
    }

    public void setupBeds() {
        Location mid = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();
        for (GamePoint point : TEAMS_BED) {
            Location bedFoot = point.getLocation();
            Block blockAtFeet = world.getBlockAt(bedFoot);

            String colorBlockBelow = world.getBlockAt(bedFoot.clone().subtract(0, 1, 0)).getType().name().split("_", 2)[0];

            BlockFace face = Utils.whereAmIRelatedTo(mid, bedFoot);
            
            blockAtFeet.setType(Material.valueOf(  colorBlockBelow + "_BED"));

            Bed feet = (Bed) Bukkit.createBlockData(Material.valueOf(  colorBlockBelow + "_BED"), (data) -> {
                ((Bed) data).setFacing(face);
                ((Bed) data).setPart(Bed.Part.FOOT);
            });
            blockAtFeet.setBlockData(feet);

            Block blockAtHead = world.getBlockAt(bedFoot.clone().add(bedFoot, face.getModX(), 0, face.getModZ()));

            Bed head = (Bed) Bukkit.createBlockData(Material.valueOf(  colorBlockBelow + "_BED"), (data) -> {
                ((Bed) data).setFacing(face);
                ((Bed) data).setPart(Bed.Part.HEAD);
            });
            blockAtHead.setBlockData(head);
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
        armurier.setRecipes(getTradesArmurier());
        this.merchantArmurier = armurier;
    }

    private List<MerchantRecipe> getTradesBatisseur() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.SANDSTONE, 4);
        MerchantRecipe sandstone = new MerchantRecipe(product, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        sandstone.addIngredient(new ItemStack(Material.BRICK));
        recipeList.add(sandstone);

        ItemStack product1 = new ItemStack(Material.END_STONE);
        MerchantRecipe endstone = new MerchantRecipe(product1, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        endstone.addIngredient(new ItemStack(Material.BRICK, 4));
        recipeList.add(endstone);

        ItemStack product2 = new ItemStack(Material.SOUL_SAND);
        MerchantRecipe soulsand = new MerchantRecipe(product2, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        soulsand.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        recipeList.add(soulsand);

        ItemStack product3 = new ItemStack(Material.WOODEN_PICKAXE);
        product3.addEnchantment(Enchantment.DIG_SPEED, 1); // Dig_speed = Efficiency
        product3.addEnchantment(Enchantment.DURABILITY, 1);
        MerchantRecipe pick1 = new MerchantRecipe(product3, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        pick1.addIngredient(new ItemStack(Material.BRICK, 10));
        recipeList.add(pick1);

        ItemStack product4 = new ItemStack(Material.STONE_PICKAXE);
        product4.addEnchantment(Enchantment.DIG_SPEED, 2);
        product4.addEnchantment(Enchantment.DURABILITY, 1);
        MerchantRecipe pick2 = new MerchantRecipe(product4, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        pick2.addIngredient(new ItemStack(Material.IRON_INGOT, 5));
        recipeList.add(pick2);

        ItemStack product5 = new ItemStack(Material.IRON_PICKAXE);
        product5.addEnchantment(Enchantment.DIG_SPEED, 4);
        product5.addEnchantment(Enchantment.DURABILITY, 3);
        product5.addEnchantment(Enchantment.SILK_TOUCH, 1);
        MerchantRecipe pick3 = new MerchantRecipe(product5, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        pick3.addIngredient(new ItemStack(Material.IRON_INGOT, 10));
        pick3.addIngredient(new ItemStack(Material.GOLD_INGOT));
        recipeList.add(pick3);

        ItemStack product6 = new ItemStack(Material.LADDER);
        MerchantRecipe ladder = new MerchantRecipe(product6, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        ladder.addIngredient(new ItemStack(Material.BRICK, 5));
        recipeList.add(ladder);



        return recipeList;
    }

    private List<MerchantRecipe> getTradesTerroriste() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.GOLDEN_SWORD);
        product.addEnchantment(Enchantment.DAMAGE_ALL, 1);
        product.addEnchantment(Enchantment.KNOCKBACK, 1);
        MerchantRecipe sword = new MerchantRecipe(product, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        sword.addIngredient(new ItemStack(Material.IRON_INGOT, 2));
        recipeList.add(sword);

        ItemStack product1 = new ItemStack(Material.GOLDEN_SWORD);
        product1.addEnchantment(Enchantment.DAMAGE_ALL, 2);
        product1.addEnchantment(Enchantment.KNOCKBACK, 1);
        product1.addEnchantment(Enchantment.DURABILITY, 1);
        MerchantRecipe sword1 = new MerchantRecipe(product1, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        sword1.addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
        recipeList.add(sword1);

        ItemStack product2 = new ItemStack(Material.DIAMOND_SWORD);
        product2.addEnchantment(Enchantment.DAMAGE_ALL, 4);
        product2.addEnchantment(Enchantment.KNOCKBACK, 2);
        MerchantRecipe sword2 = new MerchantRecipe(product2, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        sword2.addIngredient(new ItemStack(Material.EMERALD, 2));
        sword2.addIngredient(new ItemStack(Material.GOLD_INGOT, 2));
        recipeList.add(sword2);

        ItemStack product3 = new ItemStack(Material.BOW);
        product3.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        MerchantRecipe bow = new MerchantRecipe(product3, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        bow.addIngredient(new ItemStack(Material.IRON_INGOT, 6));
        recipeList.add(bow);

        ItemStack product4 = new ItemStack(Material.BOW);
        product4.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        product4.addEnchantment(Enchantment.ARROW_DAMAGE, 2);
        MerchantRecipe bow1 = new MerchantRecipe(product4, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        bow1.addIngredient(new ItemStack(Material.GOLD_INGOT, 6));
        recipeList.add(bow1);

        ItemStack product5 = new ItemStack(Material.BOW);
        product5.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        product5.addEnchantment(Enchantment.ARROW_DAMAGE, 4);
        MerchantRecipe bow2 = new MerchantRecipe(product5, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        bow2.addIngredient(new ItemStack(Material.EMERALD, 3));
        bow2.addIngredient(new ItemStack(Material.GOLD_INGOT, 6));
        recipeList.add(bow2);

        ItemStack product6 = new ItemStack(Material.ARROW);
        MerchantRecipe arrow = new MerchantRecipe(product6, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        arrow.addIngredient(new ItemStack(Material.GOLD_INGOT));
        recipeList.add(arrow);

        ItemStack product7 = new ItemStack(Material.SHIELD);
        MerchantRecipe shield = new MerchantRecipe(product7, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        arrow.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        recipeList.add(arrow);


        return recipeList;
    }

    private List<MerchantRecipe> getTradesTavernier() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.COOKED_BEEF);
        MerchantRecipe meat = new MerchantRecipe(product, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        meat.addIngredient(new ItemStack(Material.BRICK, 2));
        recipeList.add(meat);

        ItemStack product1 = new ItemStack(Material.GOLDEN_APPLE);
        MerchantRecipe gapple = new MerchantRecipe(product1, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        gapple.addIngredient(new ItemStack(Material.IRON_INGOT, 1));
        recipeList.add(gapple);

        ItemStack product2 = new ItemStack(Material.FISHING_ROD);
        MerchantRecipe fishingrod = new MerchantRecipe(product2, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        fishingrod.addIngredient(new ItemStack(Material.IRON_INGOT, 4));
        recipeList.add(fishingrod);

        ItemStack product3 = new ItemStack(Material.COBWEB);
        MerchantRecipe cobweb = new MerchantRecipe(product3, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        cobweb.addIngredient(new ItemStack(Material.BRICK, 6));
        cobweb.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        recipeList.add(cobweb);

        ItemStack product4 = new ItemStack(Material.TNT);
        MerchantRecipe bow1 = new MerchantRecipe(product4, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        bow1.addIngredient(new ItemStack(Material.IRON_INGOT, 6));
        recipeList.add(bow1);

        ItemStack product5 = new ItemStack(Material.FLINT_AND_STEEL);
        MerchantRecipe lighter = new MerchantRecipe(product5, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        lighter.addIngredient(new ItemStack(Material.GOLD_INGOT, 2));
        recipeList.add(lighter);

        ItemStack product6 = new ItemStack(Material.REDSTONE_TORCH);
        MerchantRecipe redstonetorch = new MerchantRecipe(product6, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        redstonetorch.addIngredient(new ItemStack(Material.BRICK, 15));
        recipeList.add(redstonetorch);

        ItemStack product7 = new ItemStack(Material.ENDER_PEARL);
        MerchantRecipe enderpearl = new MerchantRecipe(product7, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        enderpearl.addIngredient(new ItemStack(Material.EMERALD));
        enderpearl.addIngredient(new ItemStack(Material.GOLD_INGOT, 5));
        recipeList.add(enderpearl);

        return recipeList;
    }

    private List<MerchantRecipe> getTradesArmurier() {
        List<MerchantRecipe> recipeList = new ArrayList<>();

        ItemStack product = new ItemStack(Material.GOLDEN_LEGGINGS);
        MerchantRecipe leggings = new MerchantRecipe(product, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        leggings.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        recipeList.add(leggings);

        ItemStack product1 = new ItemStack(Material.GOLDEN_CHESTPLATE);
        MerchantRecipe chestplate = new MerchantRecipe(product1, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        chestplate.addIngredient(new ItemStack(Material.IRON_INGOT, 3));
        recipeList.add(chestplate);

        ItemStack product2 = new ItemStack(Material.IRON_HELMET);
        MerchantRecipe helmet = new MerchantRecipe(product2, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        helmet.addIngredient(new ItemStack(Material.GOLD_INGOT, 2));
        recipeList.add(helmet);

        ItemStack product3 = new ItemStack(Material.IRON_CHESTPLATE);
        MerchantRecipe chestplate1 = new MerchantRecipe(product3, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        chestplate1.addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
        recipeList.add(chestplate1);

        ItemStack product4 = new ItemStack(Material.IRON_LEGGINGS);
        MerchantRecipe leggings1 = new MerchantRecipe(product4, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        leggings1.addIngredient(new ItemStack(Material.GOLD_INGOT, 3));
        recipeList.add(leggings1);

        ItemStack product5 = new ItemStack(Material.IRON_BOOTS);
        MerchantRecipe boots = new MerchantRecipe(product5, 0, Integer.MAX_VALUE, false, 0, 1, -1, 0, true);
        boots.addIngredient(new ItemStack(Material.GOLD_INGOT, 2));
        recipeList.add(boots);

        ItemStack product6 = new ItemStack(Material.DIAMOND_CHESTPLATE);
        product6.addEnchantment(Enchantment.PROTECTION_PROJECTILE, 4);
        MerchantRecipe chestplate2 = new MerchantRecipe(product6, 0, Integer.MAX_VALUE, false, 0, -1, 1, 0, true);
        chestplate2.addIngredient(new ItemStack(Material.EMERALD, 3));
        recipeList.add(chestplate2);

        return recipeList;
    }

    public void yDeath(int yDeath) {
        this.yDeath = yDeath;
    }
    public int yDeath() {
        return this.yDeath;
    }

    public void startStopwatch() {
        new GameTimer((time) -> {
            for (Team team : Core.get().getTeamManager().getTeams().values()) {
                if (time % 10 == 9){
                    rewardTeam(team, new ItemStack(Material.IRON_INGOT));
                }
                if (time % 30 == 29){
                    rewardTeam(team, new ItemStack(Material.GOLD_INGOT));
                }
                if (time % 300 == 299){
                    rewardTeam(team, new ItemStack(Material.EMERALD));
                }
                rewardTeam(team, new ItemStack(Material.BRICK));
            }
        }, -1);
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
        Collections.shuffle(points);

        for (ItemStack stackTotal : stacks) {
            int distributed = stackTotal.getAmount();
            ItemStack stack = stackTotal.clone();
            stack.setAmount(1);

            while (true) {
                for (GamePoint gamePoint : points) {
                    Location loc = gamePoint.getLocation().clone().add(0.5, 0, 0.5);
                    Item item = this.world.dropItem(loc, stack);
                    item.setVelocity(item.getVelocity().setX(0).setZ(0));
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
     * Il n'y a jamais vraiment de raison de mettre destroyed sur {@code false}.
     * @param team L'équipe qui a perdu son lit
     * @param destroyed La valeur à mettre
     */
    public void bedDestroyed(Team team, boolean destroyed) {
        if (destroyed)
            bedDestroyed.add(team);
        else
            bedDestroyed.remove(team);
    }

}
