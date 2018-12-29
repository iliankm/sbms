package com.iliankm.sbms.auth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
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
import com.auth0.jwt.interfaces.DecodedJWT;
import com.iliankm.sbms.auth.dto.JwtDTO;
import com.iliankm.sbms.auth.dto.LoginDTO;
import com.iliankm.sbms.config.ApplicationPropertiesTestConfig;
import com.iliankm.sbms.exception.UnauthorizedException;
import com.iliankm.sbms.jwt.JwtUtil;
import com.iliankm.sbms.utils.ApplicationProperties;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class LoginServiceTest {
    
    private static final String USER_NAME = "USER1";
    private static final String PASSWORD_HASHED = "VVNFUjE=";
    private static final String PASSWORD = "USER1";
    private static final String ROLE_USER = "USER";

    @Autowired
    private LoginService loginService;
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

        @Bean
        public LoginService loginService() {
            LoginService loginService = new LoginService(jwtUtil());
            loginService.getUsers().put(USER_NAME, PASSWORD_HASHED);
            loginService.getRoles().put(USER_NAME, ROLE_USER);
            return loginService;
        }
    }
    
    @Test
    public void login_With_Valid_Credentials_Test() {
        //given
        LoginDTO loginDTO = new LoginDTO(USER_NAME, PASSWORD);
        //when
        JwtDTO jwtDTO = loginService.login(loginDTO);
        //then
        //check access token
        assertNotNull(jwtDTO.getAccessToken());
        DecodedJWT accessTokenDecoded = jwtUtil.decodeToken(jwtDTO.getAccessToken());
        assertEquals(USER_NAME, accessTokenDecoded.getSubject());
        assertEquals(new HashSet<>(Arrays.asList(ROLE_USER)), jwtUtil.getRoles(accessTokenDecoded));
        //check refresh token
        assertNotNull(jwtDTO.getRefreshToken());
        DecodedJWT refreshTokenDecoded = jwtUtil.decodeToken(jwtDTO.getRefreshToken());
        assertEquals(USER_NAME, refreshTokenDecoded.getSubject());
        assertTrue(jwtUtil.isRefreshToken(refreshTokenDecoded));
    }

    @Test(expected = UnauthorizedException.class)
    public void try_Login_With_Null_Argument_Test() {
        loginService.login(null);
    }    

    @Test(expected = UnauthorizedException.class)
    public void try_Login_With_Invalid_Password_Test() {
        //given
        LoginDTO loginDTO = new LoginDTO(USER_NAME, "aa");
        //when
        loginService.login(loginDTO);
    }
    
    @Test(expected = UnauthorizedException.class)
    public void try_Login_With_Null_Password_Test() {
        //given
        LoginDTO loginDTO = new LoginDTO(USER_NAME, null);
        //when
        loginService.login(loginDTO);
    }
    
    @Test(expected = UnauthorizedException.class)
    public void try_Login_With_Invalid_Username_Test() {
        //given
        LoginDTO loginDTO = new LoginDTO("aa", PASSWORD);
        //when
        loginService.login(loginDTO);
    } 
    
    @Test(expected = UnauthorizedException.class)
    public void try_Login_With_Null_Username_Test() {
        //given
        LoginDTO loginDTO = new LoginDTO(null, PASSWORD);
        //when
        loginService.login(loginDTO);
    }     
}
