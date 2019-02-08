package com.iliankm.sbms.consumer;

import java.util.Map;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.TestTopicService;

@Component
public class TestConsumer implements Consumer<Map<String, String>> {
    
    private final TestTopicService testTopicService;
    
    public TestConsumer(TestTopicService testTopicService) {
        this.testTopicService = testTopicService;
    }

    @Override
    @KafkaListener(topics = Topic.Names.TOPIC_TEST)
    public void listen(@Payload Map<String, String> message, @Headers Map<String, Object> headers) {
        testTopicService.process(message);
    }

}
