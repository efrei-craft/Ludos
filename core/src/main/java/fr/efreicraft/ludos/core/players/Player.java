package fr.efreicraft.ludos.core.players;

import fr.efreicraft.ludos.core.players.menus.PlayerMenus;
import fr.efreicraft.ludos.core.players.runnables.PlayerRespawnCountdown;
import fr.efreicraft.ludos.core.players.scoreboards.PlayerScoreboard;
import fr.efreicraft.ludos.core.players.scoreboards.ScoreboardField;
import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.games.GameManager;
import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.teams.Team;
import fr.efreicraft.ludos.core.utils.MessageUtils;
import fr.efreicraft.ludos.core.utils.SoundUtils;
import fr.efreicraft.ludos.core.utils.TitleUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 * Joueur des mini-jeux.
 *
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class Player {

    /**
     * Instance du joueur Bukkit.
     */
    private final org.bukkit.entity.Player playerEntity;

    /**
     * Instance du scoreboard du joueur.
     */
    private final PlayerScoreboard scoreboard;

    /**
     * Instance du menu du joueur.
     */
    private final PlayerMenus playerMenus;

    /**
     * Instance de l'équipe du joueur.
     */
    private Team team;

    /**
     * Location où le joueur doit respawn après sa mort.
     */
    private Location respawnLocation;

    private boolean ephemeralPlayer = false;

    /**
     * Constructeur du joueur.
     * @param playerEntity Instance du joueur Bukkit.
     */
    public Player(org.bukkit.entity.Player playerEntity) {
        this.playerEntity = playerEntity;
        this.playerMenus = new PlayerMenus();
        this.scoreboard = new PlayerScoreboard(this);
        this.setupScoreboard();

        if(Core.get().getGameManager().getStatus() != null
                && Core.get().getGameManager().getStatus() != GameManager.GameStatus.WAITING
                && !entity().hasPermission("ludos.admin")) {
            this.ephemeralPlayer = true;
            sendMessage(MessageUtils.ChatPrefix.SERVER, "&7Vous avez rejoint une partie &aen cours de jeu&7. Vous serez &cdéconnecté&7 à la fin de celle-ci.");
        }
    }

    /**
     * S'occupe de désinstancer les dépendances pour la destruction du joueur.
     */
    public void unload() {
        this.scoreboard.unload();
        if(getTeam() != null) {
            getTeam().removePlayer(this);
        }
    }

    /**
     * Mets en place le scoreboard du joueur selon l'état du jeu en cours.
     * Le scoreboard n'est affiché que si l'état du jeu est {@link GameManager.GameStatus#INGAME} ou {@link GameManager.GameStatus#WAITING}.
     *
     * @see GameManager.GameStatus
     * @see GameManager#setStatus(GameManager.GameStatus)
     */
    public void setupScoreboard() {
        this.scoreboard.clearFields();
        if (Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING) {
            this.scoreboard.setVisibility(true);
            this.scoreboard.setTitle("&f&lEn attente...");

            final String EMPTY = "Aucun";

            this.scoreboard.setField(
                    1,
                    new ScoreboardField(
                            "&6&lJoueurs",
                            true,
                            player1 -> {
                                ChatColor color = ChatColor.WHITE;
                                String maxString = "";
                                if(Core.get().getGameManager().getCurrentGame() != null) {
                                    Game game = Core.get().getGameManager().getCurrentGame();
                                    if(game.getMetadata().rules().minPlayers() <= Core.get().getPlayerManager().getNumberOfPlayingPlayers()) {
                                        color = ChatColor.GREEN;
                                    } else {
                                        color = ChatColor.RED;
                                    }
                                    maxString = "&7/" + game.getMetadata().rules().maxPlayers();
                                }
                                return color + "" + Core.get().getPlayerManager().getNumberOfPlayingPlayers() + maxString;
                            }
                    )
            );


            this.scoreboard.setField(
                    2,
                    new ScoreboardField(
                            "&6&lJeu",
                            player1 -> {
                                if (Core.get().getGameManager().getCurrentGame() == null) {
                                    return EMPTY;
                                } else {
                                    return Core.get().getGameManager().getCurrentGame().getMetadata().name();
                                }
                            }
                    )
            );

            this.scoreboard.setField(
                    3,
                    new ScoreboardField(
                            "&6&lCarte",
                            player1 -> {
                                if (Core.get().getGameManager().getCurrentGame() == null) {
                                    return EMPTY + "e";
                                } else {
                                    if (Core.get().getMapManager().getCurrentMap() == null) {
                                        return EMPTY + "e";
                                    } else {
                                        return Core.get().getMapManager().getCurrentMap().getName();
                                    }
                                }
                            }
                    )
            );

            this.scoreboard.setField(
                    4,
                    new ScoreboardField(
                            "&6&lEquipe",
                            player1 -> {
                                Team team1 = player1.getTeam();
                                if (team1 == null) {
                                    return EMPTY + "e";
                                } else {
                                    return team1.getName();
                                }
                            }
                    )
            );
        } else if (Core.get().getGameManager().getStatus() == GameManager.GameStatus.INGAME) {
            Game game = Core.get().getGameManager().getCurrentGame();
            this.scoreboard.setVisibility(true);
            this.scoreboard.setTitle(game.getMetadata().color() + "&l" + game.getMetadata().name());
            Core.get().getGameManager().getCurrentGame().setupScoreboard(this);
        } else {
            this.scoreboard.setVisibility(false);
        }
    }

    /**
     * Renvoie l'instance entité du joueur Bukkit.
     *
     * @return Instance entité du joueur Bukkit.
     */
    public org.bukkit.entity.Player entity() {
        return playerEntity;
    }

    /**
     * Renvoie l'instance de l'équipe du joueur.
     *
     * @return Instance de l'équipe du joueur.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Retourne le {@link PlayerScoreboard} du joueur.
     * @return Le {@link PlayerScoreboard} du joueur.
     */
    public PlayerScoreboard getBoard() {
        return scoreboard;
    }

    /**
     * Retourne le {@link PlayerMenus} du joueur.
     * @return Le {@link PlayerMenus} du joueur.
     */
    public PlayerMenus getPlayerMenus() {
        return playerMenus;
    }

    /**
     * Défini l'équipe du joueur et envoie un message de confirmation au joueur.
     *
     * @param team Nouvelle équipe du joueur.
     */
    public void setTeam(Team team) {
        this.team = team;

        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.WAITING
            || Core.get().getTeamManager().getTeams().size() > 2) {
            TextComponent msgComponent = Component.text()
                    .append(Component.text("Vous êtes désormais dans l'équipe ", NamedTextColor.GRAY))
                    .append(team.name().decoration(TextDecoration.BOLD, true))
                    .append(Component.text(".", NamedTextColor.GRAY))
                    .build();

            MessageUtils.sendMessage(
                    entity(),
                    MessageUtils.ChatPrefix.TEAM,
                    LegacyComponentSerializer.legacyAmpersand().serialize(msgComponent)
            );
        }

        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.INGAME && !team.isPlayingTeam()) {
            for (Player p : Core.get().getPlayerManager().getPlayers()) {
                if(p != this && p.getTeam().isPlayingTeam()) {
                    p.entity().hidePlayer(Core.get().getPlugin(), entity());
                }
            }
        }
    }

    /**
     * Supprime l'équipe du joueur.
     */
    public void clearTeam() {
        this.team = null;
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            for (Player p : Core.get().getPlayerManager().getPlayers()) {
                if(p != this) {
                    p.entity().showPlayer(Core.get().getPlugin(), entity());
                }
            }
        }
    }

    /**
     * Envoie un message au joueur.
     *
     * @param prefix  Préfixe du message.
     * @param message Message à envoyer.
     */
    public void sendMessage(MessageUtils.ChatPrefix prefix, String message) {
        MessageUtils.sendMessage(this.playerEntity, prefix, message);
    }

    /**
     * Envoie un title au joueur.
     * @param title     Titre du title.
     * @param subtitle  Sous-titre du title.
     * @param fadeIn    Temps d'apparition du title en secondes.
     * @param stay      Temps d'affichage du title en secondes.
     * @param fadeOut   Temps de disparition du title en secondes.
     */
    public void sendTitle(String title, String subtitle, float fadeIn, float stay, float fadeOut) {
        TitleUtils.sendTitle(this, title, subtitle, fadeIn, stay, fadeOut);
    }

    /**
     * Joue un son au joueur.
     * @param sound     Son à jouer.
     * @param volume    Volume du son.
     * @param pitch     Pitch du son.
     */
    public void playSound(Sound sound, float volume, float pitch) {
        SoundUtils.playSound(this, sound, volume, pitch);
    }

    public String toString() {
        return this.playerEntity.getName();
    }

    /**
     * Vérification pour voir si le joueur est éphémère.
     * @return Booléen indiquant si le joueur est éphémère.
     */
    public boolean isEphemeral() {
        return this.ephemeralPlayer;
    }

    /**
     * Retourne le nom du joueur, avec la couleur de son équipe s'il en a une.
     * @return Nom du joueur
     */
    public String getName() {
        return this.team == null
                ? this.playerEntity.getName()
                : LegacyComponentSerializer.legacyAmpersand().serialize(
                        Component.text(this.playerEntity.getName()).color(this.team.getColor().textColor())
                  );
    }

    /**
     * Remets le joueur à un état de jeu normal.
     */
    public void resetPlayer() {
        entity().setGameMode(GameMode.ADVENTURE);
        entity().setHealth(20);
        entity().setFoodLevel(20);
        entity().setSaturation(20);
        entity().setExhaustion(0);
        entity().setFireTicks(0);
        entity().setFallDistance(0);
        entity().setExp(0);
        entity().setLevel(0);
        entity().setAllowFlight(false);
        entity().setFlying(false);
        entity().setWalkSpeed(0.2f);
        entity().setFlySpeed(0.1f);
        entity().getInventory().clear();
        entity().getInventory().setArmorContents(null);
    }

    /**
     * Spawn le joueur dans le monde d'attente.
     */
    public void spawnAtWaitingLobby() {
        LobbyPlayerHelper.preparePlayerForLobby(this);
    }

    /**
     * Méthode appelée quand le joueur est tué.
     * @param event L'évènement de mort.
     */
    public void deathEvent(PlayerDeathEvent event) {
        event.deathMessage(null);
        if(Core.get().getGameManager().getStatus() != GameManager.GameStatus.INGAME) {
            spawnAtWaitingLobby();
        } else if(!event.isCancelled()) {
            this.respawnLocation = event.getEntity().getLocation();
            if(event.getEntity().getKiller() != null) {
                Player killer = Core.get().getPlayerManager().getPlayer(event.getEntity().getKiller());
                MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, getName() + "&7 a été tué par " + killer.getName() + "&7.");
            } else if (event.getEntity().getLastDamageCause() != null &&
                    event.getEntity().getLastDamageCause().getCause() == EntityDamageEvent.DamageCause.VOID) {
                this.respawnLocation = Core.get().getMapManager().getCurrentMap().getMiddleOfMap();
                MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, getName() + "&7 est mort en tombant dans le vide.");
            } else {
                MessageUtils.broadcastMessage(MessageUtils.ChatPrefix.GAME, getName() + "&7 est mort.");
            }
        }
    }

    /**
     * Méthode appelée quand le joueur s'apprête à réapparaitre.
     * @param event L'évènement de réapparition.
     */
    public void respawnEvent(PlayerRespawnEvent event) {
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.INGAME) {
            if(this.getTeam().isPlayingTeam() && this.respawnLocation != null) {
                event.setRespawnLocation(this.respawnLocation);
                this.respawnLocation = null;
            } else {
                event.setRespawnLocation(Core.get().getMapManager().getCurrentMap().getMiddleOfMap());
            }
        }
    }

    /**
     * Méthode appelée quand le joueur est réapparu.
     */
    public void postRespawnEvent() {
        if(Core.get().getGameManager().getStatus() == GameManager.GameStatus.INGAME
                && this.getTeam().isPlayingTeam()) {
            entity().setGameMode(GameMode.SPECTATOR);
            Game game = Core.get().getGameManager().getCurrentGame();
            if(game != null) {
                if(game.getMetadata().rules().allowRespawn()) {
                    PlayerRespawnCountdown countdown = new PlayerRespawnCountdown(this);
                    countdown.runTaskTimer(Core.get().getPlugin(), 0, 20);
                } else {
                    Team specTeam = Core.get().getTeamManager().getTeam("SPECTATORS");
                    if(specTeam != null) {
                        specTeam.addPlayer(this);
                    }
                }
            }
        }
    }
}
