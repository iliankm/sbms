package com.iliankm.sbms.config;

import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliankm.sbms.utils.ApplicationProperties;
import io.lettuce.core.cluster.ClusterClientOptions;

@EnableCaching
@Configuration
public class RedisConfig extends CachingConfigurerSupport {

    private final ApplicationProperties applicationProperties;
    private final ObjectMapper objectMapper;

    public RedisConfig(ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    @ConditionalOnProperty("redis.enabled")
    public LettuceConnectionFactory redisConnectionFactory() {
        
        ClusterClientOptions clusterClientOptions =
                        ClusterClientOptions.builder().validateClusterNodeMembership(false).build();
        
        LettuceClientConfiguration lettuceClientConfiguration =
                        LettuceClientConfiguration.builder().clientOptions(clusterClientOptions)
                                        // .commandTimeout(Duration.ofMillis(commandTimeout))
                                        // .useSsl()
                                        .build();
        
        //redis standalone config.
        if (applicationProperties.redisHosts().size() == 1) {
            
            String[] host = applicationProperties.redisHosts().iterator().next().split(":");
            
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration(host[0], Integer.valueOf(host[1]));
            
            if (StringUtils.hasText(applicationProperties.redisPassword())) {
                redisConfig.setPassword(RedisPassword.of(applicationProperties.redisPassword()));
            }
            
            return new LettuceConnectionFactory(redisConfig, lettuceClientConfiguration);
            
        } else {
            //redis cluster config.
            if (applicationProperties.redisHosts().size() > 1) {
                
                RedisClusterConfiguration redisConfig = new RedisClusterConfiguration();

                applicationProperties.redisHosts().forEach(r -> {
                    String[] parts = r.split(":");
                    redisConfig.addClusterNode(new RedisClusterNode(parts[0], Integer.valueOf(parts[1])));
                });

                if (StringUtils.hasText(applicationProperties.redisPassword())) {
                    redisConfig.setPassword(RedisPassword.of(applicationProperties.redisPassword()));
                }
                
                return new LettuceConnectionFactory(redisConfig, lettuceClientConfiguration);
            }
        }
        
        return null;
    }

    @Bean
    @ConditionalOnProperty("redis.enabled")
    public RedisTemplate<String, Object> template() {

        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));
        
        return template;
    }

    @Bean
    @ConditionalOnProperty("redis.enabled")
    public CacheManager cacheManager() {

        RedisCacheConfiguration cacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofSeconds(applicationProperties.redisCacheTtl()))
                        .disableCachingNullValues()
                        // .computePrefixWith((cacheName) -> keyPrefix + ":" + cacheName + ":")
                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper)));
        return RedisCacheManager.builder(redisConnectionFactory()).cacheDefaults(cacheConfiguration).build();
    }

}
