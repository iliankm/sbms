package com.iliankm.sbms.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.iliankm.sbms.jwt.JwtUtil;
import com.iliankm.sbms.utils.RequestAttributesUtil;
import com.iliankm.sbms.web.filter.AuthenticationFilter;
import com.iliankm.sbms.web.filter.CorrelationFilter;

@Configuration
public class FilterConfig {
    
    private final JwtUtil jwtUtil;
    
    private final RequestAttributesUtil requestAttributesUtil;
    
    public FilterConfig(JwtUtil jwtUtil, RequestAttributesUtil requestAttributesUtil) {
        this.jwtUtil = jwtUtil;
        this.requestAttributesUtil = requestAttributesUtil;
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
        return new AuthenticationFilter(jwtUtil, requestAttributesUtil);
    }
    
    @Bean
    public CorrelationFilter correlationFilter() {
        return new CorrelationFilter(requestAttributesUtil);
    }
    
}
