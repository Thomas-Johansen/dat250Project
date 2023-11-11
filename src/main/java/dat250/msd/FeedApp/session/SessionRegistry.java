package dat250.msd.FeedApp.session;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.UUID;

@Component
public class SessionRegistry {
    //TODO: Add a database for the session id
    private static final HashMap<String, String> SESSIONS = new HashMap<>();

    private final ValueOperations<String, String> redisSessionStorage;

    public SessionRegistry(final RedisTemplate<String, String> redisTemplate) {
        this.redisSessionStorage = redisTemplate.opsForValue();
    }

    public String registerSession(final String username) {
        if (username == null) {
            throw new RuntimeException("Username needs to be provided");
        }
        final String sessionId = generateSessionId();

        try {
            // You can set a timeout on the session id in redis database
            redisSessionStorage.set(sessionId, username);
            System.out.println("Using redis");
        } catch (final Exception e) {
            e.printStackTrace();
            //If the redis storage does not work fall back to hashmap
            System.out.println("Not using redis");
            SESSIONS.put(sessionId, username);
        }
        return sessionId;
    }

    public void unregisterSession(final String sessionId) {
        if (sessionId == null) {
            throw new RuntimeException("There needs to be an active session");
        }

        try {
            redisSessionStorage.getAndDelete(sessionId);
        } catch (final Exception e) {
            e.printStackTrace();
            //If the redis storage does not work fall back to hashmap
            SESSIONS.remove(sessionId);
        }
    }

    public String getUsernameForSession(final String sessionId){
        try {
            return redisSessionStorage.get(sessionId);
        } catch (final Exception e) {
            e.printStackTrace();
            //If the redis storage does not work fall back to hashmap
            return SESSIONS.get(sessionId);
        }
    }

    private String generateSessionId() {
        return new String(
                Base64.getEncoder().encode(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8))
        );
    }

    // TODO separate storage for IoT sessions? + String -> Long?
    public String registerIoTSession(String pollIdString) {
        final String IoTSessionId = generateSessionId();
        try {
            // You can set a timeout on the session id in redis database
            redisSessionStorage.set(IoTSessionId, pollIdString);
            System.out.println("Using redis");
        } catch (final Exception e) {
            e.printStackTrace();
            //If the redis storage does not work fall back to hashmap
            System.out.println("Not using redis");
            SESSIONS.put(IoTSessionId, pollIdString);
        }
        return IoTSessionId;
    }

    public String getPollIdForIoTSession(final String iotToken) {
        try {
            return redisSessionStorage.get(iotToken);
        } catch (final Exception e) {
            e.printStackTrace();
            //If the redis storage does not work fall back to hashmap
            return SESSIONS.get(iotToken);
        }
    }
}

