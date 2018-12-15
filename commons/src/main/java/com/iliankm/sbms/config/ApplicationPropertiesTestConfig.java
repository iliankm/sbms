package com.iliankm.sbms.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Profile({"test"})
@Configuration
@PropertySources({
    @PropertySource(value = "classpath:application_common.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application_common-test.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application.properties", ignoreResourceNotFound = true),
    @PropertySource(value = "classpath:application-test.properties", ignoreResourceNotFound = true)    
})
public class ApplicationPropertiesTestConfig {

}
