package com.iliankm.sbms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import com.iliankm.sbms.jwt.JwtUtil;
import com.iliankm.sbms.utils.ApplicationProperties;

@Profile({"test"})
@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application_common.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application_common-test.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application-test.properties", ignoreResourceNotFound = true)    
})
public class ApplicationTestConfig {
    
    @Bean
    public ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }
    
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(applicationProperties());
    }

}
