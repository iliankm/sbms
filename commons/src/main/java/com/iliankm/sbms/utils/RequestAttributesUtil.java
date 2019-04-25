package com.iliankm.sbms.utils;

public final class RequestAttributesUtil {
    
    public static final String JWT = "jwt";
    
    public static final String CORRELATION_ID = "CORRELATION_ID";
    
    private RequestAttributesUtil() {}
    
    public static final ThreadLocal<String> JWT_HOLDER = new ThreadLocal<>();
    
    public static final ThreadLocal<String> CORRELATION_ID_HOLDER = new ThreadLocal<>();
    
    public static String getJwt() {
        return JWT_HOLDER.get();
    }
    
    public static void setJwt(String jwt) {
        JWT_HOLDER.set(jwt);
    }
    
    public static String getCorrelationId() {
        return CORRELATION_ID_HOLDER.get();
    }
    
    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID_HOLDER.set(correlationId);
    }
    
    public static void reset() {
        JWT_HOLDER.remove();
        CORRELATION_ID_HOLDER.remove();
    }

}
