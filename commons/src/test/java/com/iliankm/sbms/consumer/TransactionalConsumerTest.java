package com.iliankm.sbms.consumer;

import com.iliankm.sbms.aspect.ConsumerAspect;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.config.JacksonConfig;
import com.iliankm.sbms.config.KafkaConsumerConfig;
import com.iliankm.sbms.config.KafkaProducerConfig;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.SenderService;
import com.iliankm.sbms.utils.RequestAttributesUtil;
import kafka.server.KafkaServer;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class TransactionalConsumerTest {
    
    private static final String CORRELATION_ID = UUID.randomUUID().toString();
    private static final Map<String, String> MESSAGE = new HashMap<>();
    static {
        MESSAGE.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
    private static final Map<String, String> MESSAGE_2 = new HashMap<>();
    static {
        MESSAGE_2.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }
    private static final Map<String, String> MESSAGE_OK = new HashMap<>();
    static {
        MESSAGE_OK.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
    }

    @ClassRule
    public static EmbeddedKafkaRule EMBEDDED_KAFKA = new EmbeddedKafkaRule(3);
    
    @BeforeClass
    public static void setupClass() {
        System.setProperty("kafka.bootstrap.servers", EMBEDDED_KAFKA.getEmbeddedKafka().getBrokersAsString());
    }

    @AfterClass
    public static void tearDown(){
        EMBEDDED_KAFKA.getEmbeddedKafka().getKafkaServers().forEach(KafkaServer::shutdown);
        EMBEDDED_KAFKA.getEmbeddedKafka().getKafkaServers().forEach(KafkaServer::awaitShutdown);
    }

    interface Service {
        void process(Map<String, String> message);
    }
    
    @Profile({"test"})
    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @Import({ApplicationTestConfig.class, JacksonConfig.class, KafkaConsumerConfig.class, KafkaProducerConfig.class})
    public static class TestConfiguration {
        @Bean
        public ConsumerAspect consumerAspect() {
            return new ConsumerAspect();
        }
        @Bean("transactionalTopicAService")
        public Service transactionalTopicAService() {
            return Mockito.mock(Service.class);
        }
        @Bean("transactionalTopicBService")
        public Service transactionalTopicBService() {
            return Mockito.mock(Service.class);
        }
        @Bean
        public Consumer<Map<String, String>> consumerTransactionalA(@Qualifier("transactionalTopicAService") Service transactionalTopicAService) {
            return new Consumer<>() {
                @Override
                @KafkaListener(topics = Topic.Names.TOPIC_TRANSACTIONAL_A, groupId = "sbms-transactions-test1", containerFactory = "transactionalKafkaListenerContainerFactory")
                public void listen(@Payload Map<String, String> message, @Headers Map<String, Object> headers) {
                    transactionalTopicAService.process(message);
                }
            };
        }
        @Bean
        public Consumer<Map<String, String>> consumerTransactionalB(@Qualifier("transactionalTopicBService") Service transactionalTopicBService) {
            return new Consumer<>() {
                @Override
                @KafkaListener(topics = Topic.Names.TOPIC_TRANSACTIONAL_B, groupId = "sbms-transactions-test2", containerFactory = "transactionalKafkaListenerContainerFactory")
                public void listen(@Payload Map<String, String> message, @Headers Map<String, Object> headers) {
                    transactionalTopicBService.process(message);
                }
            };
        }
    }

    @Autowired
    private SenderService kafkaSenderService;
    @Autowired @Qualifier("transactionalTopicAService")
    private Service transactionalTopicAService;
    @Autowired @Qualifier("transactionalTopicBService")
    private Service transactionalTopicBService;

    @Before
    public void setup() {
        RequestAttributesUtil.setCorrelationId(CORRELATION_ID);
    }
    
    @Test
    public void send_to_Transactional_TopicA_Expect_Fail() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(3);
        doAnswer(i -> {
            latch.countDown();
            kafkaSenderService.sendTransactional(Pair.of(Topic.TRANSACTIONAL_B, MESSAGE));
            throw new RuntimeException("Simulated runtime exception...");
        }).when(transactionalTopicAService).process(eq(MESSAGE));
        //when
        kafkaSenderService.sendInLocalTransaction(Pair.of(Topic.TRANSACTIONAL_A, MESSAGE));
        latch.await(5, TimeUnit.SECONDS);
        //then
        assertEquals(0, latch.getCount());
        verify(transactionalTopicAService, times(3)).process(eq(MESSAGE));
        verify(transactionalTopicBService, times(0)).process(eq(MESSAGE));
    }

    @Test
    public void send_to_Transactional_TopicB_Expect_Fail() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(3);
        doAnswer(i -> {
            latch.countDown();
            throw new RuntimeException("Simulated runtime exception...");
        }).when(transactionalTopicBService).process(eq(MESSAGE_2));
        //when
        kafkaSenderService.sendInLocalTransaction(Pair.of(Topic.TRANSACTIONAL_B, MESSAGE_2));
        latch.await(5, TimeUnit.SECONDS);
        //then
        assertEquals(0, latch.getCount());
        verify(transactionalTopicBService, times(3)).process(eq(MESSAGE_2));
    }

    @Test
    public void send_to_Transactional_TopicA_Expect_OK() throws InterruptedException {
        //given
        doAnswer(i -> {
            kafkaSenderService.sendTransactional(Pair.of(Topic.TRANSACTIONAL_B, MESSAGE_OK));
            return null;
        }).when(transactionalTopicAService).process(eq(MESSAGE_OK));
        //and
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(i -> {
            latch.countDown();
            return null;
        }).when(transactionalTopicBService).process(eq(MESSAGE_OK));
        //when
        kafkaSenderService.sendInLocalTransaction(Pair.of(Topic.TRANSACTIONAL_A, MESSAGE_OK));
        latch.await(5, TimeUnit.SECONDS);
        //then
        assertEquals(0, latch.getCount());
        verify(transactionalTopicAService, times(1)).process(eq(MESSAGE_OK));
        verify(transactionalTopicBService, times(1)).process(eq(MESSAGE_OK));
    }
}
