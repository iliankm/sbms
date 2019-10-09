package com.iliankm.sbms.consumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import kafka.server.KafkaServer;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import com.iliankm.sbms.aspect.ConsumerAspect;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.config.JacksonConfig;
import com.iliankm.sbms.config.KafkaConsumerConfig;
import com.iliankm.sbms.config.KafkaProducerConfig;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.SenderService;
import com.iliankm.sbms.service.TestTopicService;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class TestConsumerTest {
    
    private static final String CORRELATION_ID = UUID.randomUUID().toString();
    private static final Map<String, String> MESSAGE = new HashMap<>();
    static {
        MESSAGE.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
    
    @ClassRule
    public static EmbeddedKafkaRule EMBEDDED_KAFKA = new EmbeddedKafkaRule(1);
    @BeforeClass
    public static void setupClass() {
        System.setProperty("kafka.bootstrap.servers", EMBEDDED_KAFKA.getEmbeddedKafka().getBrokersAsString());
    }
    @AfterClass
    public static void tearDown() {
        EMBEDDED_KAFKA.getEmbeddedKafka().getKafkaServers().forEach(KafkaServer::shutdown);
        EMBEDDED_KAFKA.getEmbeddedKafka().getKafkaServers().forEach(KafkaServer::awaitShutdown);
    }

    @Autowired
    private SenderService kafkaSenderService;
    @Autowired
    private TestTopicService testTopicService;
    
    @Profile({"test"})
    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
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
        @Bean
        public ConsumerAspect consumerAspect() {
            return new ConsumerAspect();
        }
    }
    
    @Before
    public void setup() {
        RequestAttributesUtil.setCorrelationId(CORRELATION_ID);
    }
    
    @Test
    public void send_to_Test_Topic() throws Exception {
        //given
        CountDownLatch consumerLatch = new CountDownLatch(1);
        final StringBuilder correlationIdInConsumer = new StringBuilder();
        doAnswer(i -> {
            correlationIdInConsumer.append(RequestAttributesUtil.getCorrelationId());
            consumerLatch.countDown();
            return null;
        }).when(testTopicService).process(eq(MESSAGE));
        //when
        Thread.sleep(1000);
        kafkaSenderService.send(Topic.TEST, MESSAGE);
        //then
        //wait the latch for 5 seconds
        consumerLatch.await(5, TimeUnit.SECONDS);
        //verify that the BE service is called and with correct args
        verify(testTopicService, times(1)).process(eq(MESSAGE));
        //assert correlation id is passed in the consumer's context
        assertEquals(CORRELATION_ID, correlationIdInConsumer.toString());
    }
}
