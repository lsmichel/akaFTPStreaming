package com.example.demo.FTPserver.FTPserverImpl.inMemoryStorage;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;
import com.example.demo.FTPserver.FTPserverImpl.inMemoryStorage.RedisProperties;

@Configuration
public class EmbededRedisComponent {
	private final RedisServer redisServer;
	public EmbededRedisComponent(RedisProperties redisProperties) {
        this.redisServer = new RedisServer(redisProperties.getRedisPort());
        
    }
	
	@PostConstruct
    public void postConstruct() {
		redisServer.start();
    }
 
    @PreDestroy
    public void preDestroy() {
        redisServer.stop();
    }
}
