package fr.efreicraft.ludos.core.utils;

import fr.efreicraft.ludos.core.teams.TeamManager;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.EnumMap;
import java.util.Map;

/**
 * Utilitaire de gestion des couleurs.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class ColorUtils {

    /**
     * Enum des {@link DyeColor} au {@link Material} pour permettre de retrouver une équipe.
     * @see TeamManager#getTeamByDyeColor(DyeColor)
     */
    private static final Map<Material, DyeColor> DYE_COLOR_MAP = new EnumMap<>(Material.class);

    static {
        DYE_COLOR_MAP.put(Material.WHITE_WOOL, DyeColor.WHITE);
        DYE_COLOR_MAP.put(Material.ORANGE_WOOL, DyeColor.ORANGE);
        DYE_COLOR_MAP.put(Material.MAGENTA_WOOL, DyeColor.MAGENTA);
        DYE_COLOR_MAP.put(Material.LIGHT_BLUE_WOOL, DyeColor.LIGHT_BLUE);
        DYE_COLOR_MAP.put(Material.YELLOW_WOOL, DyeColor.YELLOW);
        DYE_COLOR_MAP.put(Material.LIME_WOOL, DyeColor.LIME);
        DYE_COLOR_MAP.put(Material.PINK_WOOL, DyeColor.PINK);
        DYE_COLOR_MAP.put(Material.GRAY_WOOL, DyeColor.GRAY);
        DYE_COLOR_MAP.put(Material.LIGHT_GRAY_WOOL, DyeColor.LIGHT_GRAY);
        DYE_COLOR_MAP.put(Material.CYAN_WOOL, DyeColor.CYAN);
        DYE_COLOR_MAP.put(Material.PURPLE_WOOL, DyeColor.PURPLE);
        DYE_COLOR_MAP.put(Material.BLUE_WOOL, DyeColor.BLUE);
        DYE_COLOR_MAP.put(Material.BROWN_WOOL, DyeColor.BROWN);
        DYE_COLOR_MAP.put(Material.GREEN_WOOL, DyeColor.GREEN);
        DYE_COLOR_MAP.put(Material.RED_WOOL, DyeColor.RED);
        DYE_COLOR_MAP.put(Material.BLACK_WOOL, DyeColor.BLACK);
    }

    /**
     * Récupérer la map des DyeColor au Material.
     * @return Map des DyeColor au Material.
     */
    public static Map<Material, DyeColor> getDyeColorMap() {
        return DYE_COLOR_MAP;
    }

    /**
     * Record de TeamColorSet.
     */
    public record TeamColorSet(NamedTextColor textColor, DyeColor dyeColor, Color bukkitColor) {

        public TeamColorSet(TeamColors teamColors) {
            this(
                    teamColors.getTeamColorSet().textColor,
                    teamColors.getTeamColorSet().dyeColor,
                    teamColors.getTeamColorSet().bukkitColor
            );
        }

    }

    /**
     * TeamColors données par défaut.
     */
    public enum TeamColors {
        AQUA(NamedTextColor.AQUA, DyeColor.CYAN, Color.AQUA),
        BLACK(NamedTextColor.BLACK, DyeColor.BLACK, Color.BLACK),
        BLUE(NamedTextColor.BLUE, DyeColor.BLUE, Color.BLUE),
        DARK_AQUA(NamedTextColor.DARK_AQUA, DyeColor.LIGHT_BLUE, Color.TEAL),
        DARK_BLUE(NamedTextColor.DARK_BLUE, DyeColor.BLUE, Color.NAVY),
        DARK_GRAY(NamedTextColor.DARK_GRAY, DyeColor.GRAY, Color.GRAY),
        DARK_GREEN(NamedTextColor.DARK_GREEN, DyeColor.GREEN, Color.GREEN),
        DARK_PURPLE(NamedTextColor.DARK_PURPLE, DyeColor.PURPLE, Color.PURPLE),
        DARK_RED(NamedTextColor.DARK_RED, DyeColor.RED, Color.MAROON),
        GOLD(NamedTextColor.GOLD, DyeColor.ORANGE, Color.ORANGE),
        GRAY(NamedTextColor.GRAY, DyeColor.LIGHT_GRAY, Color.SILVER),
        GREEN(NamedTextColor.GREEN, DyeColor.LIME, Color.LIME),
        LIGHT_PURPLE(NamedTextColor.LIGHT_PURPLE, DyeColor.MAGENTA, Color.FUCHSIA),
        RED(NamedTextColor.RED, DyeColor.RED, Color.RED),
        WHITE(NamedTextColor.WHITE, DyeColor.WHITE, Color.WHITE),
        YELLOW(NamedTextColor.YELLOW, DyeColor.YELLOW, Color.YELLOW);

        private final TeamColorSet teamColorSet;

        /**
         * Constructeur de TeamColors.
         * @param textColor Couleur du texte.
         * @param dyeColor Couleur de la laine.
         * @param bukkitColor Couleur Bukkit (utilisable pour des particules...).
         */
        TeamColors(NamedTextColor textColor, DyeColor dyeColor, Color bukkitColor) {
            this.teamColorSet = new TeamColorSet(textColor, dyeColor, bukkitColor);
        }

        /**
         * Récupérer le TeamColorSet.
         * @return TeamColorSet.
         */
        public TeamColorSet getTeamColorSet() {
            return teamColorSet;
        }
    }

}
