package com.iliankm.sbms.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

@Component
public class RequestAttributesUtil {
    
    public static final String JWT = "jwt";
    
    public static final String CORRELATION_ID = "CORRELATION_ID";
    
    public static final String NO_AUTH_MESSAGE = "NO_AUTH_MESSAGE";
    
    public void set(String name, Object value) {
        RequestContextHolder.getRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }
    
    public void reset() {
        RequestContextHolder.resetRequestAttributes();
    }

}
