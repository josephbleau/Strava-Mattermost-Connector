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

    @Value("${spring.redis.url}")
    private String redisHost;

    @Bean
    public JedisPool jedisPool() throws URISyntaxException {
        URI redisURI = new URI(redisHost);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        JedisPool pool = new JedisPool(poolConfig, redisURI);
        return pool;
    }
}
