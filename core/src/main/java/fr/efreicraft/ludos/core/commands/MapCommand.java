package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.maps.ParsedMap;
import fr.efreicraft.ludos.core.maps.exceptions.MapLoadingException;
import fr.efreicraft.ludos.core.maps.points.SpawnPoint;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Commande /map
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class MapCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        Player player = Core.get().getPlayerManager().getPlayer((org.bukkit.entity.Player) sender);
        
        if(args.length == 0) {
            player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /map <load | tp | metadata> <nomMap | equipe>");
            return false;
        }

        if(args[0].equalsIgnoreCase("load")) {
            if(args.length == 1) {
                player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /map load <name>");
                return false;
            }
            if(Core.get().getGameManager().getCurrentGame() == null) {
                player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cAucun jeu n'a été chargé.");
                return false;
            }
            player.sendMessage(MessageUtils.ChatPrefix.MAP, "&7Chargement de la map...");
            try {
                Core.get().getMapManager().loadMap(args[1]);
                player.sendMessage(MessageUtils.ChatPrefix.MAP, "&7Parsing de la map...");
            } catch (MapLoadingException e) {
                player.sendMessage(MessageUtils.ChatPrefix.MAP, "&cErreur: " + e.getMessage());
            }
            return true;
        } else if(args[0].equalsIgnoreCase("tp")) {
            if(args.length == 1) {
                player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cSyntaxe: /map tp <equipe>");
                return false;
            }
            ParsedMap map = Core.get().getMapManager().getCurrentMap();
            if(map == null) {
                player.sendMessage(MessageUtils.ChatPrefix.ADMIN, "&cAucune map n'est chargée!");
                return false;
            }
            Team team = Core.get().getTeamManager().getTeam(args[1]);
            SpawnPoint spawnPoint = map.getSpawnPoints().get(team).get(0);

            TextComponent msgComponent = Component.text()
                    .append(Component.text("Téléportation au premier point de spawn de l'équipe ", NamedTextColor.GRAY))
                    .append(team.name().decoration(TextDecoration.BOLD, true))
                    .append(Component.text("...", NamedTextColor.GRAY))
                    .build();

            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    LegacyComponentSerializer.legacyAmpersand().serialize(msgComponent)
            );
            player.entity().teleport(spawnPoint.getLocation());
            return true;
        } else if(args[0].equalsIgnoreCase("metadata")) {
            ParsedMap map = Core.get().getMapManager().getCurrentMap();
            if(map == null) {
                player.sendMessage(
                        MessageUtils.ChatPrefix.ADMIN,
                        "&cAucune map n'est chargée!"
                );
                return false;
            }
            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    "&7Informations sur la &amap chargée&7:"
            );
            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    "&7Nom: &b" + map.getName()
            );
            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    "&7Auteur: &b" + map.getAuthor()
            );
            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    "&7Points globaux utilisés: &b" + map.getGlobalPoints().size()
            );

            final String SEPARATOR_DASH = "&7 - &b";
            final String SEPARATOR_COLON = "&7: &b";

            for(String key : map.getGlobalPoints().keySet()) {
                player.sendMessage(
                        MessageUtils.ChatPrefix.MAP,
                        SEPARATOR_DASH + key + SEPARATOR_COLON + map.getGlobalPoints().get(key).size()
                );
            }
            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    "&7Points jeu utilisés: &b" + map.getGamePoints().size()
            );
            for(String key : map.getGamePoints().keySet()) {
                player.sendMessage(
                        MessageUtils.ChatPrefix.MAP,
                        SEPARATOR_DASH + key + SEPARATOR_COLON + map.getGamePoints().get(key).size()
                );
            }
            player.sendMessage(
                    MessageUtils.ChatPrefix.MAP,
                    "&7Points de spawn utilisés: &b" + map.getSpawnPoints().size()
            );
            for(Team key : map.getSpawnPoints().keySet()) {
                player.sendMessage(
                        MessageUtils.ChatPrefix.MAP,
                        SEPARATOR_DASH + key.getName() + SEPARATOR_COLON + map.getSpawnPoints().get(key).size()
                );
            }
            return true;
        } else {
            player.sendMessage(
                    MessageUtils.ChatPrefix.ADMIN,
                    "&cSyntaxe: /map <load | tp | metadata> <name>"
            );
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {
        if(args.length == 1) {
            return Arrays.asList("load", "tp", "metadata");
        } else if(args.length == 2) {
            if(Core.get().getGameManager().getCurrentGame() == null) {
                return new ArrayList<>();
            }
            if(args[0].equalsIgnoreCase("load")) {
                return Core.get().getGameManager().getCurrentGame().getMaps();
            } else if(args[0].equalsIgnoreCase("tp")) {
                Map<String, Team> teams = Core.get().getTeamManager().getTeams();
                return new ArrayList<>(teams.keySet());
            }
        }
        return new ArrayList<>();
    }
}
