package com.iliankm.sbms.utils;

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
    
    public String jwtSecret() {
        return jwtSecret;
    }
    
    public int jwtAccessTokenExpirationTime() {
        return jwtAccessTokenExpirationTime;
    }
    
    public int jwtRefreshTokenExpirationTime() {
        return jwtRefreshTokenExpirationTime;
    }
}