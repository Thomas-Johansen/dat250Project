package dat250.msd.FeedApp;

import dat250.msd.FeedApp.configuration.RedisProperties;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;

@Configuration
public class RedisImplementation {
    public RedisServer redisServer;

    public RedisImplementation(RedisProperties redisProperties) throws IOException {
        this.redisServer = new RedisServer(redisProperties.getPort());
    }

    @PostConstruct
    public void postConstruct() {
        try{
            this.redisServer.start();
        }
        catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
