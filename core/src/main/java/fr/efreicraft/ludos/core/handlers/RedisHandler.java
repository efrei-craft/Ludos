package fr.efreicraft.ludos.core.handlers;

import fr.efreicraft.animus.IRedisMessageHandler;
import fr.efreicraft.ludos.core.Core;

public class RedisHandler implements IRedisMessageHandler {

    @Override
    public void run(String... args) {
        System.out.println("Received Redis message: " + args[0]);
        switch (args[0]) {
            case "changeRequestedGame":
                Core.get().getGameManager().changeDefaultGame(args[1]);
                break;
            case "resetServer":
                Core.get().getGameManager().resetServer();
                break;
        }
    }
}
