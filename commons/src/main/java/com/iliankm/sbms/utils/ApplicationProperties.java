package com.iliankm.sbms.utils;

import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.access.token.expiration.time:15}")
    private int jwtAccessTokenExpirationTime;
    @Value("${jwt.refresh.token.expiration.time:20}")
    private int jwtRefreshTokenExpirationTime;
    
    @Value("${kafka.bootstrap.servers}")
    private String kafkaBootstrapServers;
    @Value("${kafka.group.id}")
    private String kafkaGroupId;
    
    @Value("${redis.enabled:false}")
    private boolean redisEnabled;
    @Value("#{'${redis.hosts:}'.split(',')}")
    private Set<String> redisHosts;
     @Value("${redis.password}")
    private String redisPassword;
    @Value("${redis.cache.ttl:0}")
    private int redisCacheTtl;
    
    public String jwtSecret() {
        return jwtSecret;
    }
    
    public int jwtAccessTokenExpirationTime() {
        return jwtAccessTokenExpirationTime;
    }
    
    public int jwtRefreshTokenExpirationTime() {
        return jwtRefreshTokenExpirationTime;
    }
    
    public String kafkaBootstrapServers() {
        return kafkaBootstrapServers;
    }
    
    public String kafkaGroupId() {
        return kafkaGroupId;
    }
    
    public boolean redisEnabled() {
        return redisEnabled;
    }

    public Set<String> redisHosts() {
        return redisHosts;
    }

    public String redisPassword() {
        return redisPassword;
    }

    public int redisCacheTtl() {
        return redisCacheTtl;
    }
    
}