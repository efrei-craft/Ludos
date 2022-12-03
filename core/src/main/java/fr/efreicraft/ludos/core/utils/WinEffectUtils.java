package fr.efreicraft.ludos.core.utils;

import fr.efreicraft.ludos.core.Core;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

/**
 * Classe utilitaire pour les effets de victoire.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class WinEffectUtils {

    private WinEffectUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void launchFireworks(Color color, Location pos1, Location pos2) {
        new BukkitRunnable() {
            int totalFireworks = 20;
            final Random random = new Random();

            @Override
            public void run() {
                if (totalFireworks == 0) {
                    cancel();
                    return;
                }
                Firework fw = (Firework) pos1.getWorld().spawnEntity(getRandomLocationBetween(pos1, pos2), EntityType.FIREWORK);
                FireworkMeta fwm = fw.getFireworkMeta();
                fwm.addEffect(FireworkEffect.builder().withColor(color).with(FireworkEffect.Type.BALL_LARGE).withFlicker().build());
                fwm.setPower(0);
                fw.setFireworkMeta(fwm);
                totalFireworks--;
            }

            // use Random.nextInt(int) to get a random number
            private Location getRandomLocationBetween(Location pos1, Location pos2) {
                int x;
                int y;
                int z;

                if(pos1.getBlockX() != pos2.getBlockX()) {
                    x = random.nextInt(Math.abs(pos1.getBlockX() - pos2.getBlockX())) + Math.min(pos1.getBlockX(), pos2.getBlockX());
                } else {
                    x = pos1.getBlockX();
                }
                if(pos1.getBlockY() != pos2.getBlockY()) {
                    y = random.nextInt(Math.abs(pos1.getBlockY() - pos2.getBlockY())) + Math.min(pos1.getBlockY(), pos2.getBlockY());
                } else {
                    y = pos1.getBlockY();
                }
                if(pos1.getBlockZ() != pos2.getBlockZ()) {
                    z = random.nextInt(Math.abs(pos1.getBlockZ() - pos2.getBlockZ())) + Math.min(pos1.getBlockZ(), pos2.getBlockZ());
                } else {
                    z = pos1.getBlockZ();
                }
                return new Location(pos1.getWorld(), x, y, z);
            }
        }.runTaskTimer(Core.get().getPlugin(), 0, 5);
    }

}
