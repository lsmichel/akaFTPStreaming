package com.example.demo.FTPserver.FTPserverImpl.inMemoryStorage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class EmbededRedisConfiguration {
	
	@Bean
    public LettuceConnectionFactory redisConnectionFactory(
      RedisProperties redisProperties) {
        return new LettuceConnectionFactory(
          redisProperties.getRedisHost(), 
          redisProperties.getRedisPort());
    }
 
    @Bean
    public RedisTemplate<?, ?> redisTemplate(LettuceConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }
}
