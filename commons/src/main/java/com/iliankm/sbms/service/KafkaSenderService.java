package com.iliankm.sbms.service;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.utils.RequestAttributesUtil;

@Service
public class KafkaSenderService {
    
    private static final Logger log = LoggerFactory.getLogger(KafkaSenderService.class);
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    
    private final RequestAttributesUtil requestAttributesUtil;
    
    public KafkaSenderService(KafkaTemplate<String, Object> kafkaTemplate, RequestAttributesUtil requestAttributesUtil) {
        this.kafkaTemplate = kafkaTemplate;
        this.requestAttributesUtil = requestAttributesUtil;
    }
    
    public void send(Topic topic, Object payload) {
        //prepare headers
        List<Header> headers = new LinkedList<>();
        headers.add(new RecordHeader(KafkaHeaders.CORRELATION_ID, requestAttributesUtil.get(RequestAttributesUtil.CORRELATION_ID).toString().getBytes()));
        headers.add(new RecordHeader(KafkaHeaders.TOPIC, topic.getTopicName().getBytes()));

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic.getTopicName(), null, null, null, payload, headers);

        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(producerRecord);

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> result) {
                log.info("Kafka message sent to topic {}", topic.getTopicName());
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Error sending kafka message to topic " + topic.getTopicName(), ex);
            }
        });
        
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to send message to topic " + topic.getTopicName(), e);
        }        
    }

}
