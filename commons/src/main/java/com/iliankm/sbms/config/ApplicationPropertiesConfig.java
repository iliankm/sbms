package com.iliankm.sbms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import com.iliankm.sbms.jwt.JwtUtil;
import com.iliankm.sbms.utils.ApplicationProperties;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application_common.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application_common-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)    
})
public class ApplicationPropertiesConfig {
    
    @Bean
    public ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }
    
    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil(applicationProperties());
    }
    
    @Bean
    public RequestAttributesUtil requestAttributesUtil() {
        return new RequestAttributesUtil();
    }
    
}
