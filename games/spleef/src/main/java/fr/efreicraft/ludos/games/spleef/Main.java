package fr.efreicraft.ludos.games.spleef;

import fr.efreicraft.ludos.core.games.interfaces.Game;
import fr.efreicraft.ludos.core.games.interfaces.GamePlugin;

public final class Main extends GamePlugin {

    public Main() {
        super();
    }

    @Override
    public Class<? extends Game> getGameClass() {
        return LudosGame.class;
    }


}
