package com.iliankm.sbms.consumer;

import java.util.Map;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

/**
 * @param <T> the class of the kafka message object that is going to be consumed
 */
public interface Consumer<T> {

    void listen(@Payload T message, @Headers Map<String, Object> headers);
}
