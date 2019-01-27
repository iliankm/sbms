package com.iliankm.sbms.jwt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.concurrent.ListenableFuture;
import com.iliankm.sbms.config.ApplicationTestConfig;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.service.KafkaSenderService;
import com.iliankm.sbms.utils.RequestAttributesUtil;

//@RunWith(SpringRunner.class)
@ActiveProfiles({"test"})
public class KafkaSenderServiceTest {
    
    @Autowired
    private KafkaSenderService kafkaSenderService;
    
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
        public KafkaSenderService kafkaSenderService(RequestAttributesUtil requestAttributesUtil) {
            return new KafkaSenderService(kafkaTemplate(), requestAttributesUtil);
        }
       
    }
    
    @SuppressWarnings("unchecked")
    //@Test
    public void kafkaSenderService_send() {
        //given
        String payload = "";
        ListenableFuture<SendResult<String, Object>> future = Mockito.mock(ListenableFuture.class);
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);
        //when
        kafkaSenderService.send(Topic.TEST, payload);
        //then
        //verify()
    }

}
