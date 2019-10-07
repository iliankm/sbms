package com.iliankm.sbms.consumer;

import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.TestTransactionalTopicBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("test")
@Component
public class TestTransactionalConsumerB implements Consumer<Map<String, String>> {

    private final TestTransactionalTopicBService testTransactionalTopicBService;

    @Autowired
    public TestTransactionalConsumerB(TestTransactionalTopicBService testTransactionalTopicBService) {
        this.testTransactionalTopicBService = testTransactionalTopicBService;
    }

    @Override
    @KafkaListener(topics = Topic.Names.TOPIC_TRANSACTIONAL_B, groupId = "sbms-test", containerFactory = "transactionalKafkaListenerContainerFactory")
    public void listen(@Payload Map<String, String> message, @Headers Map<String, Object> headers) {
        testTransactionalTopicBService.process(message);
    }
}
