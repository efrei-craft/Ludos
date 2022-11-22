package fr.efreicraft.ludos.core.players.menus.interfaces;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project Ludos
 */
public abstract class MenuItem {

    private TextComponent name = null;

    private List<Component> description = null;

    private IMenuClickCallback callback;

    private IMenuItemRefresh refresh;

    protected MenuItem(
            String name,
            String description,
            IMenuClickCallback callback,
            IMenuItemRefresh refresh
    ) {
        this.name = LegacyComponentSerializer.legacyAmpersand().deserialize(name).decoration(TextDecoration.ITALIC, false);
        this.description = new ArrayList<>();
        for (String line : description.split("\n")) {
            this.description.add(LegacyComponentSerializer.legacyAmpersand().deserialize(line).decoration(TextDecoration.ITALIC, false));
        }
        this.callback = callback;
        this.refresh = refresh;
    }

    protected MenuItem(
            String name,
            String description
    ) {
        this(name, description, null, null);
    }

    protected MenuItem(
            IMenuClickCallback callback,
            IMenuItemRefresh refresh
    ) {
        this.callback = callback;
        this.refresh = refresh;
    }

    public TextComponent getName() {
        if (this.refresh != null) {
            return this.refresh.refresh().getName();
        }
        return name;
    }

    public List<Component> getDescription() {
        if (this.refresh != null) {
            return this.refresh.refresh().getDescription();
        }
        return description;
    }

    public IMenuClickCallback getCallback() {
        return callback;
    }
}
