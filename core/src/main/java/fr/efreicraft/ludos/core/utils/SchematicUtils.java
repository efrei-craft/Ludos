package fr.efreicraft.ludos.core.utils;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import fr.efreicraft.ludos.core.Core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Utilitaire pour gérer les schematics.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class SchematicUtils {

    private SchematicUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Récupère un schematic depuis un fichier.
     * @param name Nom du schematic.
     * @return Schematic.
     */
    private static File getSchematicFile(String name) {
        return new File(Core.get().getPlugin().getDataFolder(), "schematics/" + name + ".schem");
    }

    /**
     * Charge un schematic dans un {@link Clipboard} WorldEdit.
     * @param name Nom du schematic.
     * @return Clipboard.
     * @throws IOException Erreur lors de la lecture du fichier schematic.
     */
    public static Clipboard loadSchematic(String name) throws IOException {
        File file = getSchematicFile(name);
        ClipboardFormat format = ClipboardFormats.findByFile(file);
        if (format == null) {
            throw new FileNotFoundException("Schematic file not found");
        }
        try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
            return reader.read();
        }
    }

    /**
     * Place un {@link Clipboard} dans un monde.
     * @param world Monde dans lequel placer le schematic.
     * @param schematic Clipboard contenant le schematic.
     * @param position Position ou coller le schematic.
     * @throws WorldEditException Erreur lors du placement du schematic.
     */
    public static void pasteSchematic(World world, Clipboard schematic, BlockVector3 position) throws WorldEditException {
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
            Operation operation = new ClipboardHolder(schematic)
                    .createPaste(editSession)
                    .to(position)
                    .ignoreAirBlocks(false)
                    .build();
            Operations.complete(operation);
        }
    }

}
