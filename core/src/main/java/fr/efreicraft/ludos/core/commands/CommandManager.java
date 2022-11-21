package fr.efreicraft.ludos.core.commands;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.IManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

import java.lang.reflect.InvocationTargetException;

/**
 * Système gérant les commandes du plugin.<br />
 * On vient récupérer les commandes déclarées dans le plugin.yml et on les associe à leur classe.<br /><br />
 *
 * Cette classe est un singleton géré par le Core.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public class CommandManager implements IManager {

    private static final String COMMAND_PACKAGE = "fr.efreicraft.ludos.core.commands";

    /**
     * Constructeur du gestionnaire de commandes. Il initialise les commandes du plugin.
     */
    public CommandManager() {
        if(Core.get().getCommandManager() != null) {
            throw new IllegalStateException("CommandManager already initialized !");
        }
        this.loadCommands();
    }

    @Override
    public void runManager() {
        // Il n'y a aucun code à exécuter après l'instanciation des managers.
    }

    private void loadCommands() {
        Core.get().getLogger().info("Registering commands...");

        Core.get().getPlugin().getDescription().getCommands().forEach((name, command) -> {
            try {
                String className = name.substring(0, 1).toUpperCase() + name.substring(1) + "Command";
                Class<?> commandClass = Class.forName(COMMAND_PACKAGE + "." + className);
                CommandExecutor executor = (CommandExecutor) commandClass.getDeclaredConstructor().newInstance();
                PluginCommand pluginCommand = Core.get().getPlugin().getCommand(name);
                assert pluginCommand != null;
                pluginCommand.setExecutor(executor);
                Core.get().getLogger().info("Registered command " + name);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

}
