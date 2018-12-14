package com.iliankm.sbms.jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Set;
import org.springframework.stereotype.Component;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.iliankm.sbms.utils.ApplicationProperties;

@Component
public class JwtUtil {
    
    private static final String ISSUER = "sbms";
    private static final String CLAIM_ROLES = "ROLES";
    
    private final ApplicationProperties applicationProperties;
    
    public JwtUtil(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }
    
    public static void main(String[] args) {
        System.out.println(LocalDateTime.now().atOffset(ZoneOffset.UTC));
        System.out.println(Instant.now());
        System.out.println(LocalDateTime.now().plusMinutes(15).atZone(ZoneId.systemDefault()).toInstant());
    }
    
    public String createAccessToken(String subject, Set<String> roles) {
        
        try {
            Algorithm algorithm = Algorithm.HMAC256(applicationProperties.jwtSecret());
            
            String token = JWT.create()
                            .withIssuer(ISSUER)
                            .withNotBefore(Date.from(Instant.now()))
                            .withExpiresAt(Date.from(LocalDateTime.now()
                                            .plusMinutes(applicationProperties.jwtAccessTokenExpirationTime())
                                            .atZone(ZoneId.systemDefault()).toInstant()))
                            .withSubject(subject)
                            .withArrayClaim(CLAIM_ROLES, roles.toArray(new String[roles.size()]))
                            .sign(algorithm);
            
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }
    
    public String createRefreshToken(String subject) {
        
        try {
            Algorithm algorithm = Algorithm.HMAC256(applicationProperties.jwtSecret());
            
            String token = JWT.create()
                            .withIssuer(ISSUER)
                            .withNotBefore(Date.from(Instant.now()))
                            .withExpiresAt(Date.from(LocalDateTime.now()
                                            .plusMinutes(applicationProperties.jwtRefreshTokenExpirationTime())
                                            .atZone(ZoneId.systemDefault()).toInstant()))
                            .withSubject(subject)
                            .sign(algorithm);
            
            return token;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }        
    }

}
