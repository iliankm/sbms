package com.iliankm.sbms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import com.iliankm.sbms.jwt.JwtUtils;
import com.iliankm.sbms.utils.AppProperties;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application_common.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application_common-${spring.profiles.active}.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application-${spring.profiles.active}.properties", ignoreResourceNotFound = true)    
})
public class ApplicationConfig {
    
    @Bean
    public AppProperties applicationProperties() {
        return new AppProperties();
    }
    
    @Bean
    public JwtUtils jwtUtil() {
        return new JwtUtils(applicationProperties());
    }

    /**
     * Needed in order to have bean validation on Components and Services
     */
    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
