package fr.efreicraft.ludos.games.blockparty;

import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.interfaces.GamePlugin;

/**
 * @see LudosGame game entrypoint.
 */
public class Main extends GamePlugin {

    public Main() {
        super();
    }

    @Override
    protected Class<? extends Game> getGameClass() {
        return LudosGame.class;
    }

}
