package com.josephbleau.stravamattermostconnector.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisConfiguration {

    private final String hostName;
    private final int port;
    private final String password;

    public RedisConfiguration(
            @Value("${spring.redis.host-name}") final String hostName,
            @Value("${spring.redis.port}") final int port,
            @Value("${spring.redis.password}") final String password) {
        this.hostName = hostName;
        this.port = port;
        this.password = password;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(hostName, port);
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration);

        if (password != null && !password.isEmpty()) {
            RedisPassword redisPassword = RedisPassword.of("p92c5e76fe540d141bd19664f43cb0d2f9fbd00dbaaee5ef1ff002316740a46dd");
            redisStandaloneConfiguration.setPassword(redisPassword);
        }

        return jedisConnectionFactory;
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory());

        return stringRedisTemplate;
    }

}
