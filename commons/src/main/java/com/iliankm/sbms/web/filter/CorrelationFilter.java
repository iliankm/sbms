package com.iliankm.sbms.web.filter;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.filter.OncePerRequestFilter;
import com.iliankm.sbms.utils.RequestAttributesUtil;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class CorrelationFilter extends OncePerRequestFilter {

    private static final String HEADER_CORREATION_ID = "Correlation-Id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                    FilterChain chain) throws ServletException, IOException {

        try {
            // get/generate corellation id
            final String correlationId = ObjectUtils.firstNonNull(
                            StringUtils.defaultIfBlank(request.getHeader(HEADER_CORREATION_ID), null),
                            UUID.randomUUID().toString());
            // set it to thread-bound request attribute
            RequestAttributesUtil.set(RequestAttributesUtil.CORRELATION_ID, correlationId);
            // set it to slf4j
            MDC.put("correlation.id", correlationId);

            chain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }

}
