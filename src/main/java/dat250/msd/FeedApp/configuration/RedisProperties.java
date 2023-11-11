package dat250.msd.FeedApp.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedisProperties
{
    private int redisPort;
    private String redisHost;

    public RedisProperties(
            @Value("${spring.data.redis.port}") int redisPort,
            @Value("${spring.data.redis.host})") String redisHost)
    {
        this.redisPort = redisPort;
        this.redisHost = redisHost;
    }

    public Integer getPort() {
        return redisPort;
    }
    public String getHost() {
        return redisHost;
    }
}
