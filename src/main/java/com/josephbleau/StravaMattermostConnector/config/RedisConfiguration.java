package com.josephbleau.StravaMattermostConnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class RedisConfiguration {

    private final String redisHost;

    public RedisConfiguration(@Value("${spring.redis.url}") final String redisHost) {
        this.redisHost = redisHost;
    }

    @Bean
    public JedisPool jedisPool() throws URISyntaxException {
        URI redisURI = new URI(redisHost);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        return new JedisPool(poolConfig, redisURI);
    }

}
