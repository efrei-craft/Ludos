package fr.efreicraft.ludos.games.arena;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.runnables.GameTimer;
import fr.efreicraft.ludos.core.games.TeamWin;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.ActionBarUtils;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class GameLogic {

    private static final int GAME_TIMER = 60 * 3;

    private final HashMap<Team, Integer> teamKills = new HashMap<>();

    private int time = 0;

    private GameTimer timer;

    public void preparePlayerToSpawn(Player player) {
        player.entity().setGameMode(GameMode.ADVENTURE);
        ItemStack sword = new ItemStack(Material.IRON_SWORD);
        sword.addEnchantment(Enchantment.DURABILITY, 3);
        player.entity().getInventory().addItem(sword);
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

    public int getTeamKills(Team team) {
        return teamKills.getOrDefault(team, 0);
    }

    public void addKill(Team team) {
        teamKills.put(team, teamKills.getOrDefault(team, 0) + 1);
    }

    public void startTimer() {
        timer = new GameTimer(timeLambda -> {
            if(timeLambda == 0) {
                makeBestTeamWin();
            } else if (timeLambda % 60 == 0 && timeLambda != GAME_TIMER) {
                int minutes = timeLambda / 60;
                ActionBarUtils.broadcastActionBar("&c&l" + minutes + " minute" + (minutes != 1 ? "s" : "") + " pour faire un maximum de kills!");
                SoundUtils.broadcastSound(Sound.ENTITY_BLAZE_HURT, 1, 0.8f);
            }
            this.time = timeLambda;
        }, GAME_TIMER);
    }

    public void makeBestTeamWin() {
        Team vikingsTeam = Core.get().getTeamManager().getTeam("VIKINGS");
        Team romainsTeam = Core.get().getTeamManager().getTeam("ROMAINS");

        if (getTeamKills(vikingsTeam) > getTeamKills(romainsTeam)) {
            Core.get().getGameManager().getCurrentGame().setWinnerAndEndGame(new TeamWin(vikingsTeam));
        } else if (getTeamKills(vikingsTeam) < getTeamKills(romainsTeam)) {
            Core.get().getGameManager().getCurrentGame().setWinnerAndEndGame(new TeamWin(romainsTeam));
        } else {
            MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&eEx aequo! &7Les deux équipes ont le même nombre de kills donc personne ne gagne...");
            Core.get().getGameManager().getCurrentGame().setWinnerAndEndGame(null);
        }
    }

    public void stopTimer() {
        if(timer != null) {
            timer.cancel();
        }
    }

    public String getTimerString() {
        return GameTimer.getTimeString(time);
    }

}
