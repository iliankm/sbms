package com.iliankm.sbms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource("classpath:application_common.properties"),
    @PropertySource("classpath:application_common-${spring.profiles.active}.properties"),
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:application-${spring.profiles.active}.properties")    
})
public class ApplicationPropertiesConfig {

}
