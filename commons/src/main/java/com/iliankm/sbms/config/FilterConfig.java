package com.iliankm.sbms.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.iliankm.sbms.web.filter.CorrelationFilter;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<CorrelationFilter> correlationFilterBean() {
        final FilterRegistrationBean<CorrelationFilter> filterRegBean = new FilterRegistrationBean<>();
        filterRegBean.setFilter(new CorrelationFilter());
        filterRegBean.setEnabled(Boolean.TRUE);
        filterRegBean.setName("CorrelationFilter");
        filterRegBean.setAsyncSupported(Boolean.FALSE);
        filterRegBean.setOrder(0);
        return filterRegBean;
    }

}
