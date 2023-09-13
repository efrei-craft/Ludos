package fr.efreicraft.ludos.games.spleef;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.players.LudosPlayer;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Random;

public class GameLogic {

    private static ItemStack THE_SHOVEL;

    private static final int SD_TIMER = 120;
    private static final int WACKINESS_TIMER = 120;
    public boolean suddenDeath = false;
    public boolean redTextFlasher = false;
    public int time;

    public static ItemStack getTheShovel() {
        return THE_SHOVEL;
    }

    public void setupPlayers() {
        Core.get().getPlayerManager().getPlayingPlayers().forEach(player -> {
            player.entity().setGameMode(org.bukkit.GameMode.SURVIVAL);
            player.entity().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 420, 1, false, false, false));
        });
    }

    public void setupTheShovel() {
        List<Material> possibleTypes = List.of(
                Material.WOODEN_SHOVEL,
                Material.STONE_SHOVEL,
                Material.IRON_SHOVEL,
                Material.GOLDEN_SHOVEL,
                Material.DIAMOND_SHOVEL,
                Material.NETHERITE_SHOVEL,
                Material.STICK,
                Material.DEAD_BUSH
        );

        List<String> possibleNames = List.of(
                "&ees hora de XD",
                "&ea&rh&eu&rh&eu&rh&eu &rm&ea&rs&et&re&er&rc&el&ra&es&rs"
        );

        THE_SHOVEL = new ItemStack(possibleTypes.get(new Random().nextInt(possibleTypes.size())));
        ItemMeta itemMeta = THE_SHOVEL.getItemMeta();
        itemMeta.displayName(LegacyComponentSerializer.legacyAmpersand().deserialize(possibleNames.get(new Random().nextInt(possibleNames.size()))));
        THE_SHOVEL.setItemMeta(itemMeta);
        THE_SHOVEL.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.DIG_SPEED, new Random().nextInt(50, 999));
    }

    public void giveTheInventory() {
        Core.get().getPlayerManager().getPlayingPlayers().forEach(player -> player.entity().getInventory().setItem(0, THE_SHOVEL));
        Core.get().getPlayerManager().getPlayingPlayers().forEach(player -> player.entity().getInventory().setItem(1, new ItemStack(Material.SNOWBALL, 8)));
    }

    public void startTimer() {
        GameTimer timer = new GameTimer(timeLambda -> {
            if (timeLambda == 0) {
                suddenDeathTask();
                SoundUtils.broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
            }
            time = timeLambda;
        }, SD_TIMER);
    }

    private void suddenDeathTask() {
        suddenDeath = true;
        Core.get().getMapManager().getCurrentMap().getWorld().setTime(21);

        for (LudosPlayer player : Core.get().getPlayerManager().getPlayingPlayers()) {
            player.setupScoreboard();
        }

        new GameTimer(timeLambda -> {
            for (LudosPlayer player : Core.get().getPlayerManager().getPlayingPlayers()) {
                Block blockGone = getHighestBlockBelow(player.entity().getLocation());
                if (blockGone == null) {
                    continue;
                }

                blockGone = blockGone.getLocation().add(new Random().nextInt(-1, 2), 0, new Random().nextInt(-1, 2)).getBlock();
                if (player.entity().getLocation().getY() - blockGone.getLocation().getY() > 3) {
                    continue;
                }
                if (blockGone.getType() != Material.LAVA) {
                    blockGone.setType(Material.AIR);
                }
            }
            redTextFlasher = !redTextFlasher;

            if (timeLambda > WACKINESS_TIMER) {
                for (LudosPlayer player : Core.get().getPlayerManager().getPlayingPlayers()) {
                    Random random = new Random();
                    if (random.nextFloat() < 0.15f) {
                        player.entity().getWorld().createExplosion(
                                player.entity().getLocation().add(random.nextFloat(-.9f, .9f), random.nextFloat(1.8f), random.nextFloat(-.9f, .9f)),
                                1.885f,
                                false,
                                false
                        );
                    }
                }
            }
        }, -1, 10);
    }

    private Block getHighestBlockBelow(Location location) {
        for (int y = location.getBlockY(); y > -64; y--) {
            Block block = location.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());
            if (block.getType() != Material.AIR) {
                return block;
            }
        }
        return null;
    }
}
