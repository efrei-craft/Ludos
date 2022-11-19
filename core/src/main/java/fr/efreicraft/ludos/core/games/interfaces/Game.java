package fr.efreicraft.ludos.core.games.interfaces;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.*;
import fr.efreicraft.ludos.core.games.annotations.GameMetadata;
import fr.efreicraft.ludos.core.maps.MapLoadingException;
import fr.efreicraft.ludos.core.players.Player;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import org.bukkit.*;
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

    private GameWinner winner;

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
        Core.get().getLogger().info("Preparing server for " + this.getClass().getName() + "...");

        GameMetadata metadata = getMetadata();
        MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&7Le prochain jeu sera " + metadata.color() + metadata.name() + "&7!");

        List<String> maps = getMaps();
        if (maps.isEmpty()) {
            Core.get().getLogger().warning("No maps available for " + this.getClass().getName() + "!");
        }

        Core.get().getTeamManager().loadTeams(this.getTeamRecords());
        Core.get().getTeamManager().dispatchAllPlayersInTeams();
        try {
            Core.get().getMapManager().loadMap(maps.get(random.nextInt(maps.size())));
        } catch (MapLoadingException e) {
            Core.get().getLogger().log(Level.SEVERE, "Erreur lors du chargement de la map.", e);
        }
    }

    private void dispatchAndSpawnTeamPlayers(Team team) {
        if(team.isPlayingTeam()) {
            for (Player player : team.getPlayers()) {
                team.spawnPlayer(player);
            }
        } else {
            Location mapMiddle = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();
            for (Player player : team.getPlayers()) {
                player.entity().teleport(mapMiddle);
                team.spawnPlayer(player);
            }
        }
    }

    private void startRunnableCountdown() {
        GameCountdown countdown = new GameCountdown();
        countdown.runTaskTimer(Core.get().getPlugin(), 0, 20);
    }

    private void broadcastGameInfo() {
        MessageUtils.broadcast("");
        MessageUtils.broadcast("&7&m--------------------------------------");
        MessageUtils.broadcast("  " + this.getMetadata().color() + ChatColor.BOLD + this.getMetadata().name());
        MessageUtils.broadcast("");
        MessageUtils.broadcast("  &f" + this.getMetadata().description());
        MessageUtils.broadcast("");
        MessageUtils.broadcast("  &7Carte: "
                + this.getMetadata().color() + "&l" + Core.get().getMapManager().getCurrentMap().getName()
                + "&7 par " + this.getMetadata().color() + Core.get().getMapManager().getCurrentMap().getAuthor()
        );
        MessageUtils.broadcast("&7&m--------------------------------------");
        MessageUtils.broadcast("");
    }

    /**
     * Méthode standardisée de démarrage de jeu
     */
    public void startGame() {
        Core.get().getLogger().info("Starting " + this.getClass().getName() + "...");

        for (Team team : Core.get().getTeamManager().getTeams().values()) {
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
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            return false;
        }
        if(this.getMetadata().rules().minPlayers() > Core.get().getPlayerManager().getNumberOfPlayingPlayers()) {
            if(Core.get().getPlayerManager().getNumberOfPlayingPlayers() > 0) {
                Player lastPlayer = Core.get().getPlayerManager().getPlayingPlayers().iterator().next();
                if(lastPlayer != null) {
                    this.winner = new PlayerWin(lastPlayer);
                }
            }
            if(this.winner == null) {
                MessageUtils.broadcast(MessageUtils.ChatPrefix.GAME, "&cIl n'y a plus assez de joueurs pour continuer le jeu.");
            }
            Core.get().getGameManager().setStatus(GameManager.GameStatus.ENDING);
            return true;
        }
        return false;
    }

    @Override
    public void beginGame() {
        // log here
        Core.get().getLogger().info("Beginning " + this.getClass().getName() + " game logic...");
        eventManager.registerMinigameEvents();
    }

    /**
     * Méthode standardisée de fin de jeu
     */
    public void endGame() {
        Core.get().getLogger().info("Ending " + this.getClass().getName() + "...");

        int secondsDelay = 5;

        if(winner != null) {
            secondsDelay = 10;
            winner.winEffect();
        }

        eventManager.unregisterMinigameEvents();
        Bukkit.getScheduler().runTaskLater(Core.get().getPlugin(), () -> {
            Core.get().getMapManager().unloadMap();
            Core.get().getTeamManager().unloadTeams();
            Core.get().getGameManager().setStatus(GameManager.GameStatus.WAITING);
        }, (long) 20 * secondsDelay);
    }

    /**
     * Méthode récupérant les cartes disponibles pour le jeu
     *
     * @return Liste des cartes disponibles en String
     */
    public List<String> getMaps() {
        return Core.get().getMapManager().getMapsForGame(this);
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

    /**
     * Change le joueur ou l'équipe gagnant(e) du jeu.
     * Peut-être soit {@link fr.efreicraft.ludos.core.games.PlayerWin} soit {@link fr.efreicraft.ludos.core.games.TeamWin}
     * @param winner Le joueur ou l'équipe gagnant(e)
     */
    public void setWinnerAndEndGame(GameWinner winner) {
        this.winner = winner;
        Core.get().getGameManager().setStatus(GameManager.GameStatus.ENDING);
    }
}
