package com.iliankm.sbms.aspect;

import java.util.Map;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@Aspect
@Configuration
public class ConsumerAspect {
    
    private static final String LOG_KEY_CORRELATION_ID = "correlation.id";

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* com.iliankm.sbms.consumer.Consumer.listen (Object, java.util.Map<String, Object>)) && args(message, headers, ..)")
    public void beforeListen(@Payload Object message, @Headers Map<String, Object> headers) {
        
        RequestAttributesUtil.reset();
        
        String correlationId = headerValue(headers.get(KafkaHeaders.CORRELATION_ID));
        String topicName = headerValue(headers.get(KafkaHeaders.TOPIC));
        
        RequestAttributesUtil.setCorrelationId(correlationId);
        
        MDC.put(LOG_KEY_CORRELATION_ID, correlationId);
        
        log.info("Kafka message for topic " + topicName + " received.");
    }
    
    @After("execution(* com.iliankm.sbms.consumer.Consumer.listen (Object, java.util.Map<String, Object>)) && args(message, headers, ..)")
    public void afterListen(@Payload Object message, @Headers Map<String, Object> headers) {
        
        String topicName = headerValue(headers.get(KafkaHeaders.TOPIC));
        
        log.info("Kafka message for topic " + topicName + " processed.");
        
        RequestAttributesUtil.reset();
    }
    
    @AfterThrowing("execution(* com.iliankm.sbms.consumer.Consumer.listen (Object, java.util.Map<String, Object>)) && args(message, headers, ..)")
    public void afterThrowing(@Payload Object message, @Headers Map<String, Object> headers) {
        
        String topicName = headerValue(headers.get(KafkaHeaders.TOPIC));
        
        log.info("Kafka message for topic " + topicName + " failed.");
        
        RequestAttributesUtil.reset();
    }
    
    private String headerValue(Object value) {
        return value instanceof byte[] ? new String((byte[]) value) : "";
    }
}
