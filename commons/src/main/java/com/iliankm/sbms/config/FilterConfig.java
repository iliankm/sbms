package com.iliankm.sbms.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.iliankm.sbms.jwt.JwtUtils;
import com.iliankm.sbms.web.filter.AuthenticationFilter;
import com.iliankm.sbms.web.filter.CorrelationFilter;

@Configuration
public class FilterConfig {
    
    private final JwtUtils jwtUtil;
    
    public FilterConfig(JwtUtils jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public FilterRegistrationBean<CorrelationFilter> correlationFilterBean() {
        final FilterRegistrationBean<CorrelationFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(correlationFilter());
        filterRegBean.setEnabled(Boolean.TRUE);
        filterRegBean.setName("CorrelationFilter");
        filterRegBean.setAsyncSupported(Boolean.FALSE);
        filterRegBean.setOrder(0);
        return filterRegBean;
    }
    
    @Bean
    public AuthenticationFilter authenticationFilter() {
        return new AuthenticationFilter(jwtUtil);
    }
    
    @Bean
    public CorrelationFilter correlationFilter() {
        return new CorrelationFilter();
    }
    
}
