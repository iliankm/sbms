package com.iliankm.sbms.consumer;

import java.util.UUID;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.config.JacksonConfig;
import com.iliankm.sbms.config.KafkaConsumerConfig;
import com.iliankm.sbms.config.KafkaProducerConfig;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.KafkaSenderService;
import com.iliankm.sbms.service.TestTopicService;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class TestConsumerTest {
    
    private static final String CORRELATION_ID = UUID.randomUUID().toString();
    private static final String MESSAGE = UUID.randomUUID().toString();
    
    @ClassRule
    public static KafkaEmbedded EMBEDDED_KAFKA = new KafkaEmbedded(1);
    
    @BeforeClass
    public static void setup() {
        System.setProperty("kafka.bootstrap.servers", EMBEDDED_KAFKA.getBrokersAsString());
    }
    
    @Autowired
    private KafkaSenderService kafkaSenderService;
    
    @Autowired
    private TestTopicService testTopicService;
    @Autowired
    private TestConsumer testConsumer;
    
    @Profile({"test"})
    @Configuration
    @Import({ApplicationTestConfig.class, JacksonConfig.class, KafkaConsumerConfig.class, KafkaProducerConfig.class})
    public static class TestConfiguration {
        @Bean
        public TestTopicService testTopicService() {
            return Mockito.mock(TestTopicService.class);
        } 
        @Bean
        public TestConsumer testConsumer() {
            return new TestConsumer(testTopicService());
        }
    }
    
    @Test
    public void send_to_Test_Topic() {
        //given
        RequestAttributesUtil.setCorrelationId(CORRELATION_ID);
        //when
        kafkaSenderService.send(Topic.TEST, MESSAGE);
        //then
        
    }

}
