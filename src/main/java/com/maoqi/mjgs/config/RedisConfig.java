package com.maoqi.mjgs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Configuration

public class RedisConfig {
	/**
	 * 自定义缓存管理器
	 */
	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory factory) {
		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
 
		Set<String> cacheNames = new HashSet<>();
		cacheNames.add("mjgs:cache:getOnlineCounter");
 
		ConcurrentHashMap<String, RedisCacheConfiguration> configMap = new ConcurrentHashMap<>();
		configMap.put("mjgs:cache:getOnlineCounter", config.entryTtl(Duration.ofSeconds(10L)));//有效期1分钟
		
		//需要先初始化缓存名称，再初始化其它的配置。
		RedisCacheManager cacheManager = RedisCacheManager.builder(factory).initialCacheNames(cacheNames).withInitialCacheConfigurations(configMap).build();
 
		return cacheManager;
	}
}