package com.iliankm.sbms.consumer;

import com.iliankm.sbms.aspect.ConsumerAspect;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.config.JacksonConfig;
import com.iliankm.sbms.config.KafkaConsumerConfig;
import com.iliankm.sbms.config.KafkaProducerConfig;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.SenderService;
import com.iliankm.sbms.service.TestTransactionalTopicAService;
import com.iliankm.sbms.service.TestTransactionalTopicBService;
import com.iliankm.sbms.utils.RequestAttributesUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
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
public class TestTransactionalConsumerTest {
    
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
        MESSAGE_OK.put("SPECIAL_KEY", UUID.randomUUID().toString());
    }

    @ClassRule
    public static EmbeddedKafkaRule EMBEDDED_KAFKA = new EmbeddedKafkaRule(3);
    
    @BeforeClass
    public static void setupClass() {
        System.setProperty("kafka.bootstrap.servers", EMBEDDED_KAFKA.getEmbeddedKafka().getBrokersAsString());
    }

    @Autowired
    private SenderService kafkaSenderService;
    @Autowired
    private TestTransactionalTopicBService testTransactionalTopicBService;
    
    @Profile({"test"})
    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @Import({ApplicationTestConfig.class, JacksonConfig.class, KafkaConsumerConfig.class, KafkaProducerConfig.class})
    public static class TestConfiguration {
        @Bean
        public TestTransactionalTopicAService testTransactionalTopicAService(SenderService senderService) {
            return new TestTransactionalTopicAService(senderService);
        }
        @Bean
        public TestTransactionalTopicBService testTransactionalTopicBService() {
            return Mockito.mock(TestTransactionalTopicBService.class);
        }
        @Bean
        public TestTransactionalConsumerA testTransactionalConsumerA(TestTransactionalTopicAService testTransactionalTopicAService) {
            return new TestTransactionalConsumerA(testTransactionalTopicAService);
        }
        @Bean
        public TestTransactionalConsumerB testTransactionalConsumerB(TestTransactionalTopicBService testTransactionalTopicBService) {
            return new TestTransactionalConsumerB(testTransactionalTopicBService);
        }
        @Bean
        public ConsumerAspect consumerAspect() {
            return new ConsumerAspect();
        }
    }
    
    @Before
    public void setup() throws InterruptedException {
        RequestAttributesUtil.setCorrelationId(CORRELATION_ID);
        //give time EMBEDDED_KAFKA to start
        Thread.sleep(5000);
    }
    
    @Test
    public void send_to_Transactional_TopicA_Expect_Fail() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(i -> {
            latch.countDown();
            return null;
        }).when(testTransactionalTopicBService).process(eq(MESSAGE));
        //when
        kafkaSenderService.sendInLocalTransaction(Pair.of(Topic.TRANSACTIONAL_A, MESSAGE));
        latch.await(5, TimeUnit.SECONDS);
        //then
        assertEquals(1, latch.getCount());
        verify(testTransactionalTopicBService, times(0)).process(eq(MESSAGE));
    }

    @Test
    public void send_to_Transactional_TopicB_Expect_Fail() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(3);
        doAnswer(i -> {
            latch.countDown();
            throw new RuntimeException("Simulated runtime exception...");
        }).when(testTransactionalTopicBService).process(eq(MESSAGE_2));
        //when
        kafkaSenderService.sendInLocalTransaction(Pair.of(Topic.TRANSACTIONAL_B, MESSAGE_2));
        latch.await(5, TimeUnit.SECONDS);
        //then
        assertEquals(0, latch.getCount());
        verify(testTransactionalTopicBService, times(3)).process(eq(MESSAGE_2));
    }

    @Test
    public void send_to_Transactional_TopicA_Expect_OK() throws InterruptedException {
        //given
        CountDownLatch latch = new CountDownLatch(1);
        doAnswer(i -> {
            latch.countDown();
            return null;
        }).when(testTransactionalTopicBService).process(eq(MESSAGE_OK));
        //when
        kafkaSenderService.sendInLocalTransaction(Pair.of(Topic.TRANSACTIONAL_A, MESSAGE_OK));
        latch.await(5, TimeUnit.SECONDS);
        //then
        assertEquals(0, latch.getCount());
        verify(testTransactionalTopicBService, times(1)).process(eq(MESSAGE_OK));
    }
}
