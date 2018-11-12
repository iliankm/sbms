package com.iliankm.sbms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@PropertySources({
    @PropertySource("classpath:application.properties"),
    @PropertySource("classpath:application-${spring.profiles.active}.properties")
})
public class ApplicationPropertiesConfig {

}
