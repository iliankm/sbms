package com.iliankm.sbms.consumer;

import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.TestTransactionalTopicAService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Profile("test")
@Component
public class TestTransactionalConsumerA implements Consumer<Map<String, String>> {

    private final TestTransactionalTopicAService testTransactionalTopicAService;

    @Autowired
    public TestTransactionalConsumerA(TestTransactionalTopicAService testTransactionalTopicAService) {
        this.testTransactionalTopicAService = testTransactionalTopicAService;
    }

    @Override
    @KafkaListener(topics = Topic.Names.TOPIC_TRANSACTIONAL_A, groupId = "sbms-test", containerFactory = "transactionalKafkaListenerContainerFactory")
    public void listen(@Payload Map<String, String> message, @Headers Map<String, Object> headers) {
        testTransactionalTopicAService.process(message);
    }
}
