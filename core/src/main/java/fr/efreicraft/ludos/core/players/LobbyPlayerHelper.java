package fr.efreicraft.ludos.core.players;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.players.menus.ChestMenu;
import fr.efreicraft.ludos.core.players.menus.ItemStackMenuItem;
import fr.efreicraft.ludos.core.players.menus.PlayerInventoryMenu;
import fr.efreicraft.ludos.core.players.menus.interfaces.MenuItem;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.ColorUtils;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe permettant de gérer les joueurs en lobby.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public class LobbyPlayerHelper {

    private LobbyPlayerHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Prépare le joueur à spawn dans le lobby d'attente.
     * @param player Joueur à préparer.
     */
    public static void preparePlayerForLobby(LudosPlayer player) {
        player.entity().teleport(Core.get().getMapManager().getLobbyWorld().getSpawnLocation().add(-0.5, 1, -0.5));
        player.resetPlayer();

        preparePlayerItems(player);
    }

    /**
     * Prépare les items de lobby du joueur.
     * @param player Joueur à préparer.
     */
    public static void preparePlayerItems(LudosPlayer player) {
        List<MenuItem> items = new ArrayList<>();

        if(Core.get().getTeamManager().getTeams().size() > 2) {
            items.add(
                    new ItemStackMenuItem(
                            0,
                            () -> {
                                Material material = null;
                                if(player.getTeam() != null) {
                                    material = ColorUtils.getWoolByDyeColor(player.getTeam().getColor().dyeColor());
                                }
                                if(material == null) {
                                    material = Material.WHITE_WOOL;
                                }

                                return new ItemStackMenuItem(
                                        new ItemStack(material),
                                        "&6Choix d'équipe&r &7• Clic droit",
                                        "&7Clic droit pour choisir une équipe."
                                );
                            },
                            event -> openTeamSelectionMenu(player)
                    )
            );
        }

        items.add(
                new ItemStackMenuItem(
                        new ItemStack(Material.RED_BED),
                        8,
                        "&cRevenir au hub&r &7• Clic droit",
                        "&7Clic droit pour retourner au hub.",
                        event -> player.sendMessage(MessageUtils.ChatPrefix.SERVER, "&cRetour au hub...")
                )
        );

        player.getPlayerMenus().setMenu(
                "LOBBY_ITEMS",
                new PlayerInventoryMenu(
                        player,
                        items
                )
        ).show();
    }

    /**
     * Ouvre le menu de sélection d'équipe.
     * @param player Joueur à préparer.
     */
    public static void openTeamSelectionMenu(LudosPlayer player) {
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.WAITING) {
            player.sendMessage(MessageUtils.ChatPrefix.GAME, "&cVous ne pouvez pas changer d'équipe en cours de partie.");
            return;
        }

        List<MenuItem> items = new ArrayList<>();
        int slot = 0;
        for(Team team : Core.get().getTeamManager().getPlayingTeams().values()) {
            String teamName = LegacyComponentSerializer.legacyAmpersand().serialize(
                    Component.text("Équipe ").color(team.getColor().textColor()).append(team.name())
            );
            items.add(
                    new ItemStackMenuItem(
                            slot,
                            () -> {
                                List<String> teamPlayers = new ArrayList<>();
                                for(LudosPlayer teamPlayer : team.getPlayers()) {
                                    teamPlayers.add("&7• " + teamPlayer.getName());
                                }
                                if(teamPlayers.isEmpty()) {
                                    teamPlayers.add("&8Personne.");
                                }
                                Material material = Material.WHITE_WOOL;
                                if(ColorUtils.getWoolByDyeColor(team.getColor().dyeColor()) != null) {
                                    material = ColorUtils.getWoolByDyeColor(team.getColor().dyeColor());
                                }
                                return new ItemStackMenuItem(
                                        new ItemStack(material),
                                        teamName,
                                        "&7Membres de l'équipe:\n"
                                                + String.join("\n", teamPlayers)
                                                + "\n\n&8» &6Cliquez pour rejoindre l'équipe."
                                );
                            },
                            event -> team.addPlayer(player)
                    )
            );
            slot++;
        }

        player.getPlayerMenus().setMenu(
                "TEAM_SELECTION",
                new ChestMenu(
                        player,
                        "&8» &6Choix d'équipe",
                        18,
                        items
                )
        ).show();
    }

}
