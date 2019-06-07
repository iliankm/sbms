package com.iliankm.sbms.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.concurrent.ListenableFuture;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class SenderServiceTest {
    
    private static final String PAYLOAD = UUID.randomUUID().toString();
    
    private static final String CORRELATION_ID = UUID.randomUUID().toString(); 
    
    @Autowired
    private SenderService senderService;
    
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    
    @Profile({"test"})
    @Configuration
    @Import(ApplicationTestConfig.class)
    public static class TestConfiguration {
        
        @SuppressWarnings("unchecked")
        @Bean
        public KafkaTemplate<String, Object> kafkaTemplate() {
            return Mockito.mock(KafkaTemplate.class);
        }
        @Bean
        public SenderService kafkaSenderService() {
            return new SenderService(kafkaTemplate());
        }
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void kafkaSenderService_send() throws InterruptedException, ExecutionException {
        //given
        ListenableFuture<SendResult<String, Object>> future = Mockito.mock(ListenableFuture.class);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        RequestAttributesUtil.setCorrelationId(CORRELATION_ID);
        //when
        senderService.send(Topic.TEST, PAYLOAD);
        //then
        //verify kafkaTemplate.send was called with correct argument
        List<Header> headers = new LinkedList<>();
        headers.add(new RecordHeader(KafkaHeaders.CORRELATION_ID, CORRELATION_ID.getBytes()));
        headers.add(new RecordHeader(KafkaHeaders.TOPIC, Topic.TEST.getTopicName().getBytes()));
        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(Topic.TEST.getTopicName(), null, null, null, PAYLOAD, headers);
        verify(kafkaTemplate, times(1)).send(eq(producerRecord));
        //verify the get() method of the Future returned by kafkaTemplate.send was invoked
        verify(future, times(1)).get();
    }

}
