package fr.efreicraft.ludos.core.clients.listeners;

import fr.efreicraft.ludos.core.Core;
import io.lettuce.core.pubsub.RedisPubSubListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class PubSubListener implements RedisPubSubListener<String, String> {

    @Override
    public void message(String channel, String message) {
        try {
            if(channel.equals(InetAddress.getLocalHost().getHostName())) {
                String[] parts = message.split("##");
                String action = parts[0];
                String data = parts[1];
                if(action.equals("SET_GAME")) {
                    if(data.equals("NONE")) {
                        Core.get().getGameManager().resetServer();
                    } else {
                        Core.get().getGameManager().changeDefaultGame(data);
                    }
                } else if(action.equals("AUTO_GAME_START")) {
                    Core.get().getGameManager().setAutoGameStart(Boolean.parseBoolean(data));
                }
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void message(String pattern, String channel, String message) {
        // Useless
    }

    @Override
    public void subscribed(String channel, long count) {
        Core.get().getLogger().info(
                String.format("Subscribed to channel %s (count: %d)", channel, count)
        );
    }

    @Override
    public void psubscribed(String pattern, long count) {
        // Useless
    }

    @Override
    public void unsubscribed(String channel, long count) {
        Core.get().getLogger().info("Unsubscribed from channel " + channel + " (count: " + count + ")");
    }

    @Override
    public void punsubscribed(String pattern, long count) {
        // Useless
    }

}
