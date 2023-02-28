package fr.efreicraft.ludos.core.clients;

import fr.efreicraft.ludos.core.Core;
import fr.efreicraft.ludos.core.clients.listeners.PubSubListener;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Antoine B. {@literal <antoine@jiveoff.fr>}
 */
public class RedisClient {

    private io.lettuce.core.RedisClient client;

    private boolean ready = false;

    public RedisClient() {
        // check if REDIS_HOST is set
        if(System.getenv("REDIS_HOST") == null) {
            Core.get().getLogger().warning("REDIS_HOST is not set, please set it in your environment variables.");
        } else {
            client = io.lettuce.core.RedisClient.create(
                    "redis://"
                            + System.getenv("REDIS_PASSWORD")
                            + "@" + System.getenv("REDIS_HOST")
                            + ":" + System.getenv("REDIS_PORT") + "/" + System.getenv("REDIS_DB")
            );
            this.subscribe();
            this.ready = true;
        }
    }

    private void subscribe() {
        StatefulRedisPubSubConnection<String, String> connection = client.connectPubSub();
        connection.addListener(new PubSubListener());
        try {
            connection.sync().subscribe(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Établit une connexion avec le serveur Redis.
     * @return La connexion.
     */
    public StatefulRedisConnection<String, String> connect() {
        return client.connect();
    }

    /**
     * Établit une connexion PubSub avec le serveur Redis.
     * @return La connexion.
     */
    public StatefulRedisPubSubConnection<String, String> connectPubSub() {
        return client.connectPubSub();
    }

    /**
     * Vérifie si le client est prêt à être utilisé.
     * @return true si le client est prêt, false sinon.
     */
    public boolean isReady() {
        return this.ready;
    }

}
