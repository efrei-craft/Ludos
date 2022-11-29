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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class GameLogic {

    private static final int GAME_TIMER = 60 * 3;

    private final HashMap<Team, Integer> teamKills = new HashMap<>();

    private final HashMap<Player, Integer> playerKillstreak = new HashMap<>();

    private final HashMap<Player, Integer> playerBestKillstreak = new HashMap<>();

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
    public void resetKillstreak(Player player) {
        playerKillstreak.put(player, 0);
    }

    public void addPlayerKill(Player player) {
        playerKillstreak.put(player, getPlayerKillstreak(player) + 1);
        if(playerBestKillstreak.getOrDefault(player, 0) < playerKillstreak.getOrDefault(player, 0)) {
            playerBestKillstreak.put(player, playerKillstreak.getOrDefault(player, 0));
        }
        player.entity().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, 1));
        announceKillstreak(player);
    }

    private void announceKillstreak(Player player) {
        int killstreak = getPlayerKillstreak(player);

        if (killstreak == 3) {
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, player.getName() + "&r&7 est &aincroyable (3 kills)&7 !");
            SoundUtils.broadcastSound(Sound.ENTITY_SKELETON_DEATH, 1, 1);
        } else if (killstreak == 6) {
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME,player.getName() + "&r&7 est &einarrêtable (6 kills)&7 !");
            SoundUtils.broadcastSound(Sound.ENTITY_BAT_DEATH, 1, 1);
        } else if (killstreak == 9) {
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, player.getName() + "&r&7 est &6&llégendaire (9 kills)&7 !");
            SoundUtils.broadcastSound(Sound.ENTITY_BLAZE_DEATH, 1, 1);
        } else if (killstreak == 12) {
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, player.getName() + "&r&7 est &c&lmonstrueux (12 kills)&r&7 !");
            SoundUtils.broadcastSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        } else if (killstreak == 15) {
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, player.getName() + "&r&7 est &f&k!!&r &b&lDIVIN (15 kills) &f&k!!&r");
            SoundUtils.broadcastSound(Sound.ENTITY_WITHER_SPAWN, 1, 1);
        }
    }

    public int getPlayerKillstreak(Player player) {
        return playerKillstreak.getOrDefault(player, 0);
    }

    public int getPlayerBestKillstreak(Player player) {
        return playerBestKillstreak.getOrDefault(player, 0);
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
            MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, "&eEx aequo! &7Les deux équipes ont le même nombre de kills, donc personne ne gagne...");
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

    public boolean isNotDeathMatch(){
        return this.time > 15;
    }

}
