package fr.jiveoff.efrei.minigames.games.arena;

import fr.jiveoff.efrei.minigames.core.players.Player;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GameLogic {

    public void preparePlayerToSpawn(Player player) {
        player.entity().setGameMode(GameMode.ADVENTURE);
        player.entity().getInventory().addItem(new org.bukkit.inventory.ItemStack(Material.STONE_SWORD));
        ItemStack[] armor = new ItemStack[4];
        armor[0] = new ItemStack(Material.LEATHER_BOOTS);
        armor[1] = new ItemStack(Material.LEATHER_LEGGINGS);
        armor[2] = new ItemStack(Material.LEATHER_CHESTPLATE);
        armor[3] = new ItemStack(Material.LEATHER_HELMET);
        for (ItemStack item : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(player.getTeam().getColor().bukkitColor());
            item.setItemMeta(meta);
        }
        player.entity().getInventory().setArmorContents(armor);
    }

}
