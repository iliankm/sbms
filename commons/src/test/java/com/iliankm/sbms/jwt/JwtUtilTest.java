package com.iliankm.sbms.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.config.ApplicationPropertiesTestConfig;
import com.iliankm.sbms.utils.ApplicationProperties;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class JwtUtilTest {
    
    private static final String SUBJECT = "SUBJECT";
    private static final String ROLE = "ROLE";

    @Autowired
    private JwtUtil jwtUtil;

    @Profile({"test"})
    @Configuration
    @Import(ApplicationPropertiesTestConfig.class)
    public static class TestConfiguration {
        @Bean
        public ApplicationProperties applicationProperties() {
            return new ApplicationProperties();
        }

        @Bean
        public JwtUtil jwtUtil() {
            return new JwtUtil(applicationProperties());
        }
    }
    
    @Test
    public void createAccessToken_Test() {
        //when
        String jwt = jwtUtil.createAccessToken(SUBJECT, new HashSet<>(Arrays.asList(ROLE)));
        //then
        assertTrue(StringUtils.hasText(jwt));
    }

    @Test
    public void createRefreshToken_Test() {
        //when
        String jwt = jwtUtil.createRefreshToken(SUBJECT);
        //then
        assertTrue(StringUtils.hasText(jwt));
    }
    
    @Test(expected = JWTDecodeException.class)
    public void decodeInvalidToken_Test() {
        jwtUtil.decodeToken("");
    }
    
    @Test
    public void decodeValidToken_Test() {
        //given
        String jwt = jwtUtil.createAccessToken(SUBJECT, new HashSet<>(Arrays.asList(ROLE)));
        //when
        DecodedJWT decodedJwt = jwtUtil.decodeToken(jwt);
        //then
        assertEquals(SUBJECT, decodedJwt.getSubject());
        assertEquals(new HashSet<>(Arrays.asList(ROLE)), jwtUtil.getRoles(decodedJwt));
        assertFalse(jwtUtil.isRefreshToken(decodedJwt));
    }
    
    @Test
    public void decodeValidRefreshToken_Test() {
        //given
        String jwt = jwtUtil.createRefreshToken(SUBJECT);
        //when
        DecodedJWT decodedJwt = jwtUtil.decodeToken(jwt);
        //then
        assertEquals(SUBJECT, decodedJwt.getSubject());
        assertEquals(Collections.emptySet(), jwtUtil.getRoles(decodedJwt));
        assertTrue(jwtUtil.isRefreshToken(decodedJwt));
    }

}
