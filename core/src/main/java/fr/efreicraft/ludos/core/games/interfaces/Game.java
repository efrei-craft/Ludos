package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameCountdown;
import fr.efreicraft.ludos.core.games.GameEventManager;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.maps.MapLoadingException;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

/**
 * Classe abstraite représentant un jeu.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 * @project EFREI-Minigames
 */
public abstract class Game implements IGame {

    private final Random random = new Random();
    
    private final GameEventManager eventManager;
    private Listener eventListenerInstance;

    /**
     * Constructeur de jeu.
     */
    protected Game() {
        this.eventManager = new GameEventManager(this);
    }

    /**
     * Méthode préparant le jeu
     */
    public void prepareServer() {
        Core.getInstance().getLogger().info("Preparing server for " + this.getClass().getName() + "...");

        GameMetadata metadata = getMetadata();
        MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&7Le prochain jeu sera " + metadata.color() + metadata.name() + "&7!");

        List<String> maps = getMaps();
        if (maps.isEmpty()) {
            Core.getInstance().getLogger().warning("No maps available for " + this.getClass().getName() + "!");
        }

        Core.getInstance().getTeamManager().loadTeams(this.getTeamRecords());
        Core.getInstance().getTeamManager().dispatchAllPlayersInTeams();
        try {
            Core.getInstance().getMapManager().loadMap(maps.get(random.nextInt(maps.size())));
        } catch (MapLoadingException e) {
            Core.getInstance().getLogger().log(Level.SEVERE, "Erreur lors du chargement de la map.", e);
        }
    }

    private void dispatchAndSpawnTeamPlayers(Team team) {
        if(team.isPlayingTeam()) {
            for (Player player : team.getPlayers()) {
                team.spawnPlayer(player);
            }
        } else {
            Location mapMiddle = Core.getInstance().getMapManager().getCurrentMap().getMiddleOfMap();
            for (Player player : team.getPlayers()) {
                player.entity().teleport(mapMiddle);
                team.spawnPlayer(player);
            }
        }
    }

    private void startRunnableCountdown() {
        GameCountdown countdown = new GameCountdown();
        countdown.runTaskTimer(Core.getInstance().getPlugin(), 0, 20);
    }

    private void broadcastGameInfo() {
        MessageUtils.broadcast("");
        MessageUtils.broadcast("&7&m--------------------------------------");
        MessageUtils.broadcast("  " + this.getMetadata().color() + ChatColor.BOLD + this.getMetadata().name());
        MessageUtils.broadcast("");
        MessageUtils.broadcast("  &f" + this.getMetadata().description());
        MessageUtils.broadcast("");
        MessageUtils.broadcast("  &7Carte: "
                + this.getMetadata().color() + "&l" + Core.getInstance().getMapManager().getCurrentMap().getName()
                + "&7 par " + this.getMetadata().color() + Core.getInstance().getMapManager().getCurrentMap().getAuthor()
        );
        MessageUtils.broadcast("&7&m--------------------------------------");
        MessageUtils.broadcast("");
    }

    /**
     * Méthode standardisée de démarrage de jeu
     */
    public void startGame() {
        Core.getInstance().getLogger().info("Starting " + this.getClass().getName() + "...");

        for (Team team : Core.getInstance().getTeamManager().getTeams().values()) {
            dispatchAndSpawnTeamPlayers(team);
        }

        broadcastGameInfo();
        startRunnableCountdown();
    }

    /**
     * Vérifie si le jeu doit se terminer
     * @return true si le jeu doit se terminer, false sinon
     */
    public boolean checkIfGameHasToBeEnded() {
        if(Core.getInstance().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            return false;
        }
        if(this.getMetadata().minPlayers() > Core.getInstance().getPlayerManager().getNumberOfPlayingPlayers()) {
            MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&cIl n'y a plus assez de joueurs pour continuer le jeu!");
            Core.getInstance().getGameManager().setStatus(GameManager.GameStatus.ENDING);
            return true;
        }
        return false;
    }

    @Override
    public void beginGame() {
        // log here
        Core.getInstance().getLogger().info("Beginning " + this.getClass().getName() + " game logic...");
        eventManager.registerMinigameEvents();
    }

    /**
     * Méthode standardisée de fin de jeu
     */
    public void endGame() {
        Core.getInstance().getLogger().info("Ending " + this.getClass().getName() + "...");
        eventManager.unregisterMinigameEvents();
        Bukkit.getScheduler().runTaskLater(Core.getInstance().getPlugin(), () -> {
            Core.getInstance().getMapManager().unloadMap();
            Core.getInstance().getTeamManager().unloadTeams();
            Core.getInstance().getGameManager().setStatus(GameManager.GameStatus.WAITING);
        }, (long) 20 * 5);
    }

    /**
     * Méthode récupérant les cartes disponibles pour le jeu
     *
     * @return Liste des cartes disponibles en String
     */
    public List<String> getMaps() {
        return Core.getInstance().getMapManager().getMapsForGame(this);
    }

    /**
     * Méthode récupérant les métadonnées du jeu à travers son annotation {@link GameMetadata}
     *
     * @return Les métadonnées du jeu
     */
    public GameMetadata getMetadata() {
        return this.getClass().getAnnotation(GameMetadata.class);
    }


    /**
     * Minigame should provide a bukkit event listener, either by :
     * - overriding this method
     * - call the {@link #setEventListener(Listener)} method before the {@link #startGame} lifecycle.
     * @return null by default, or the class instance defined by {@link #setEventListener(Listener)}.
     */
    @Override
    public Listener getEventListener() {
        return this.eventListenerInstance;
    }

    /**
     * Allow child classes to register their event listener by calling this setter instead of implementing a concrete
     * getter.
     * @param eventListenerInstance a bukkit event listener instance
     */
    public void setEventListener(Listener eventListenerInstance) {
        this.eventListenerInstance = eventListenerInstance;
    }
}
