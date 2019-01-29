package com.iliankm.sbms.web.filter;

import java.io.IOException;
import java.util.UUID;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import com.iliankm.sbms.utils.RequestAttributesUtil;

public class CorrelationFilter extends OncePerRequestFilter {

    private static final String HEADER_CORRELATION_ID = "Correlation-Id";
    
    private static final String LOG_KEY_CORRELATION_ID = "correlation.id";
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain chain) throws ServletException, IOException {

        try {
            // get/generate corellation id
            final String correlationId = ObjectUtils.firstNonNull(
                            StringUtils.defaultIfBlank(request.getHeader(HEADER_CORRELATION_ID), null),
                            UUID.randomUUID().toString());
            // set it to thread-bound request attribute
            RequestAttributesUtil.setCorrelationId(correlationId);
            // set it to slf4j
            MDC.put(LOG_KEY_CORRELATION_ID, correlationId);

            chain.doFilter(request, response);

        } finally {
            MDC.clear();
            RequestAttributesUtil.reset();
        }
    }

}
