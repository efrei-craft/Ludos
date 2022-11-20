package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.ParsedMap;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import fr.efreicraft.ludos.core.utils.WinEffectUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.util.Ticks;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Interface définissant les gagnants des mini-jeux.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public interface GameWinner {

    /**
     * Couleur des feux d'artifice.
     * @return Couleur des feux d'artifice.
     */
    Color getFireworkColor();

    /**
     * Liste les joueurs gagnants.
     * @return Joueurs gagnants.
     */
    List<Player> getPlayers();

    /**
     * Nom du gagnant, peut-être accompagné par le préfixe "L'équipe" si une équipe a gagné.
     * @return Nom du gagnant.
     */
    String getWinnerColoredName();

    /**
     * Déchaine les effets de victoire.
     */
    default void winEffect() {
        launchWinEffects(getFireworkColor());

        for(Player p : getPlayers()) {
            p.entity().setVelocity(new Vector(0, 1, 0));
            new BukkitRunnable() {
                @Override
                public void run() {
                    p.entity().setAllowFlight(true);
                    p.entity().setFlying(true);
                }
            }.runTaskLater(Core.get().getPlugin(), 10);
        }

        Game game = Core.get().getGameManager().getCurrentGame();
        ParsedMap map = Core.get().getMapManager().getCurrentMap();

        MessageUtils.broadcast("");
        MessageUtils.broadcast("&7&m--------------------------------------");
        MessageUtils.broadcast("  " + game.getMetadata().color() + ChatColor.BOLD + game.getMetadata().name());
        MessageUtils.broadcast("");
        MessageUtils.broadcast("  " + getWinnerColoredName() + " &fa gagné la partie!");
        MessageUtils.broadcast("");
        MessageUtils.broadcast("  &7Carte: "
                + game.getMetadata().color() + "&l" + map.getName()
                + "&7 par " + game.getMetadata().color() + map.getAuthor()
        );
        MessageUtils.broadcast("&7&m--------------------------------------");
        MessageUtils.broadcast("");

        for(Player p : Core.get().getPlayerManager().getPlayers()) {
            p.entity().showTitle(Title.title(
                    LegacyComponentSerializer.legacyAmpersand().deserialize(getWinnerColoredName()),
                    Component.text("a gagné la partie !").color(NamedTextColor.GRAY),
                    Title.Times.times(Ticks.duration(10), Ticks.duration(100), Ticks.duration(15))
            ));
        }
    }

    private void launchWinEffects(Color color) {
        int yToUse;
        Location firstBoundary = Core.get().getMapManager().getCurrentMap().getGlobalPoints().get("BOUNDARY").get(0).getLocation();
        Location secondBoundary = Core.get().getMapManager().getCurrentMap().getGlobalPoints().get("BOUNDARY").get(1).getLocation();
        yToUse = Math.max(firstBoundary.getBlockY(), secondBoundary.getBlockY());

        Location pos1ForFireworks = new Location(
                Core.get().getMapManager().getCurrentMap().getWorld(),
                Math.min(firstBoundary.getBlockX(), secondBoundary.getBlockX()),
                yToUse,
                Math.min(firstBoundary.getBlockZ(), secondBoundary.getBlockZ())
        );

        Location pos2ForFireworks = new Location(
                Core.get().getMapManager().getCurrentMap().getWorld(),
                Math.max(firstBoundary.getBlockX(), secondBoundary.getBlockX()),
                yToUse,
                Math.max(firstBoundary.getBlockZ(), secondBoundary.getBlockZ())
        );

        WinEffectUtils.launchFireworks(
                color,
                pos1ForFireworks,
                pos2ForFireworks
        );
    }
}