package com.iliankm.sbms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application.yml", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application-${spring.profiles.active}.yml", ignoreResourceNotFound = true)    
})
public class ApplicationConfig {
}
