package com.iliankm.sbms.utils;

public final class RequestAttributesUtil {
    
    public static final String JWT = "jwt";
    
    public static final String CORRELATION_ID = "CORRELATION_ID";
    
    public static final String NO_AUTH_MESSAGE = "NO_AUTH_MESSAGE";
    
    private RequestAttributesUtil() {}
    
    public static final ThreadLocal<String> JWT_HOLDER = new ThreadLocal<>();
    
    public static final ThreadLocal<String> CORRELATION_ID_HOLDER = new ThreadLocal<>();
    
    public static final ThreadLocal<String> NO_AUTH_MESSAGE_HOLDER = new ThreadLocal<>();
    
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
    
    public static String getNoAuthMessage() {
        return NO_AUTH_MESSAGE_HOLDER.get();
    }
    
    public static void setNoAuthMessage(String noAuthMessage) {
        NO_AUTH_MESSAGE_HOLDER.set(noAuthMessage);
    }
    
    public static void reset() {
        JWT_HOLDER.remove();
        CORRELATION_ID_HOLDER.remove();
        NO_AUTH_MESSAGE_HOLDER.remove();
    }

}
