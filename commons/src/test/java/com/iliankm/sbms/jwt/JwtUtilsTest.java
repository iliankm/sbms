package com.iliankm.sbms.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.utils.AppProperties;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class JwtUtilsTest {
    
    private static final String SUBJECT = "SUBJECT";
    private static final String ROLE = "ROLE";

    @Autowired
    private JwtUtils jwtUtils;

    @Profile({"test"})
    @Configuration
    @Import(ApplicationTestConfig.class)
    public static class TestConfiguration {}
    
    @Test
    public void createAccessToken_Test() {
        //when
        String jwt = jwtUtils.createAccessToken(SUBJECT, new HashSet<>(Arrays.asList(ROLE)));
        //then
        assertTrue(StringUtils.hasText(jwt));
    }

    @Test
    public void createRefreshToken_Test() {
        //when
        String jwt = jwtUtils.createRefreshToken(SUBJECT);
        //then
        assertTrue(StringUtils.hasText(jwt));
    }
    
    @Test(expected = JWTDecodeException.class)
    public void decodeInvalidToken_Test() {
        jwtUtils.decodeToken("");
    }
    
    @Test(expected = TokenExpiredException.class)
    public void decodeExpiredToken_Test() throws InterruptedException {
        //given
        AppProperties applicationProperties = Mockito.mock(AppProperties.class);
        when(applicationProperties.jwtAccessTokenExpirationTime()).thenReturn(-1);
        when(applicationProperties.jwtSecret()).thenReturn("SECRET");
        JwtUtils jwtUtil = new JwtUtils(applicationProperties);
        String jwt = jwtUtil.createAccessToken(SUBJECT, new HashSet<>(Arrays.asList(ROLE)));
        //when
        jwtUtil.decodeToken(jwt);
    }
    
    @Test
    public void decodeValidToken_Test() {
        //given
        String jwt = jwtUtils.createAccessToken(SUBJECT, new HashSet<>(Arrays.asList(ROLE)));
        //when
        DecodedJWT decodedJwt = jwtUtils.decodeToken(jwt);
        //then
        assertEquals(SUBJECT, decodedJwt.getSubject());
        assertEquals(new HashSet<>(Arrays.asList(ROLE)), jwtUtils.getRoles(decodedJwt));
        assertFalse(jwtUtils.isRefreshToken(decodedJwt));
    }
    
    @Test
    public void decodeValidRefreshToken_Test() {
        //given
        String jwt = jwtUtils.createRefreshToken(SUBJECT);
        //when
        DecodedJWT decodedJwt = jwtUtils.decodeToken(jwt);
        //then
        assertEquals(SUBJECT, decodedJwt.getSubject());
        assertEquals(Collections.emptySet(), jwtUtils.getRoles(decodedJwt));
        assertTrue(jwtUtils.isRefreshToken(decodedJwt));
    }

}
