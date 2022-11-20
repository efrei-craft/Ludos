package fr.efreicraft.ludos.games.arena;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameTimer;
import fr.efreicraft.ludos.core.games.TeamWin;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.GameMode;
import org.bukkit.Material;
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
        player.entity().getInventory().addItem(new ItemStack(Material.STONE_SWORD));
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
                MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&7Plus que &c" + minutes + " minutes&7 pour faire un maximum de kills!");
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
        timer.cancel();
    }

    public String getTimerString() {
        return GameTimer.getTimeString(time);
    }

}
