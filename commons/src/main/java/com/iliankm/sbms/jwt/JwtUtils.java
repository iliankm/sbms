package com.iliankm.sbms.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.utils.AppProperties;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtils {
    
    private static final String ISSUER = "sbms";
    private static final String CLAIM_ROLES = "ROLES";
    private static final String CLAIM_IS_REFRESH_TOKEN = "CLAIM_IS_REFRESH_TOKEN";
    
    private final AppProperties applicationProperties;
    private final Algorithm algorithm;
    
    public JwtUtils(AppProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
        this.algorithm = Algorithm.HMAC256(applicationProperties.jwtSecret());
    }
    
    public String createAccessToken(String subject, Set<String> roles) {
        
        try {
            return JWT.create()
                    .withIssuer(ISSUER)
                    .withNotBefore(Date.from(Instant.now()))
                    .withExpiresAt(Date.from(LocalDateTime.now()
                            .plusMinutes(applicationProperties.jwtAccessTokenExpirationTime())
                            .atZone(ZoneId.systemDefault()).toInstant()))
                    .withSubject(subject)
                    .withArrayClaim(CLAIM_ROLES, roles.toArray(new String[0]))
                    .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    public String createRefreshToken(String subject) {

        try {
            return JWT.create()
                            .withIssuer(ISSUER)
                            .withNotBefore(Date.from(Instant.now()))
                            .withExpiresAt(Date.from(LocalDateTime.now()
                                            .plusMinutes(applicationProperties.jwtRefreshTokenExpirationTime())
                                            .atZone(ZoneId.systemDefault()).toInstant()))
                            .withSubject(subject)
                            .withClaim(CLAIM_IS_REFRESH_TOKEN, Boolean.TRUE)
                            .sign(algorithm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    public DecodedJWT decodeToken(String token) {
        
        return JWT.require(algorithm).withIssuer(ISSUER).build().verify(token);
    }
    
    public Set<String> getRoles(DecodedJWT decodedJWT) {
        Claim claim = decodedJWT.getClaim(CLAIM_ROLES);
        return !claim.isNull() ? Set.copyOf(claim.asList(String.class)) : Collections.emptySet();
    }
    
    public boolean isRefreshToken(DecodedJWT decodedJWT) {
        Claim claim = decodedJWT.getClaim(CLAIM_IS_REFRESH_TOKEN);
        return !claim.isNull() ? claim.asBoolean() : false;
    }
}
