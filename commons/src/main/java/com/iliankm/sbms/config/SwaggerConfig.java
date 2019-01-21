package com.iliankm.sbms.config;

import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .securitySchemes(Arrays.asList(new ApiKey("jwt", HttpHeaders.AUTHORIZATION, "header")))
            .securityContexts(Arrays.asList(securityContext()))
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.iliankm.sbms"))
            .paths(PathSelectors.any())
            .build()
            .pathMapping("/");
    }
    
    private SecurityContext securityContext() {
        return SecurityContext.builder()
                        .securityReferences(Arrays.asList(new SecurityReference("jwt", scopes())))
                        .forPaths(PathSelectors.regex("/api.*")).build();
    }
    
    private AuthorizationScope[] scopes() {
        AuthorizationScope[] scopes = {
                        new AuthorizationScope("read", "for read operations"),
                        new AuthorizationScope("write", "for write operations"),
                        new AuthorizationScope("foo", "Access foo API")};
        return scopes;
    }
    
}
