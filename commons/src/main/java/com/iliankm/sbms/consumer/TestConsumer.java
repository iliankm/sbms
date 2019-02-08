package com.iliankm.sbms.consumer;

import java.util.Map;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.TestTopicService;

@Component
public class TestConsumer implements Consumer<Map<String, String>> {
    
    private final TestTopicService testTopicService;
    
    public TestConsumer(TestTopicService testTopicService) {
        this.testTopicService = testTopicService;
    }

    @KafkaListener(topics = Topic.Names.TOPIC_TEST)
    @Override
    public void listen(Map<String, String> message, Map<String, Object> headers) {
        testTopicService.process(message);
    }

}
