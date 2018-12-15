package com.iliankm.sbms.jwt;

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
import org.springframework.util.StringUtils;
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

}
