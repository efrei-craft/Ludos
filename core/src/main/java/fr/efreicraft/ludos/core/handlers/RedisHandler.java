package fr.efreicraft.ludos.core.handlers;

import fr.efreicraft.animus.IRedisMessageHandler;
import fr.efreicraft.ludos.core.Core;

public class RedisHandler implements IRedisMessageHandler {

    @Override
    public void run(String... args) {
        switch (args[0]) {
            case "changeRequestedGame":
                Core.get().getGameManager().changeDefaultGame(args[1]);
                break;
        }
    }
}
