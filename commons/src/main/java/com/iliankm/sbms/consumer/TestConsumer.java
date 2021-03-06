package com.iliankm.sbms.consumer;

import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.TestTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("test")
@Component
public class TestConsumer implements Consumer<Map<String, String>> {
    
    private final TestTopicService testTopicService;
    
    @Autowired
    public TestConsumer(TestTopicService testTopicService) {
        this.testTopicService = testTopicService;
    }

    @Override
    @KafkaListener(topics = Topic.Names.TOPIC_TEST, groupId = "sbms-test")
    public void listen(@Payload Map<String, String> message, @Headers Map<String, Object> headers) {
        testTopicService.process(message);
    }

}
