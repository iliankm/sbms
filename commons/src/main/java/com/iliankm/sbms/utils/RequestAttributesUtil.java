package com.iliankm.sbms.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

public final class RequestAttributesUtil {
    
    public static final String JWT = "jwt";
    
    public static final String CORRELATION_ID = "CORRELATION_ID";
    
    public static final String NO_AUTH_MESSAGE = "NO_AUTH_MESSAGE";
    
    private RequestAttributesUtil() {}
    
    public static void set(String name, Object value) {
        RequestContextHolder.getRequestAttributes().setAttribute(name, value, RequestAttributes.SCOPE_REQUEST);
    }
    
    @SuppressWarnings("unchecked")
    public static <T> T get(String name) {
        return (T) RequestContextHolder.getRequestAttributes().getAttribute(name, RequestAttributes.SCOPE_REQUEST);
    }

}
