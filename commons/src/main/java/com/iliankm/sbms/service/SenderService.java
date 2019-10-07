package com.iliankm.sbms.service;

import com.iliankm.sbms.enums.Topic;
import com.iliankm.sbms.utils.RequestAttributesUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class SenderService {

    private static final Logger log = LoggerFactory.getLogger(SenderService.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTemplate<String, Object> transactionalKafkaTemplate;

    @Autowired
    public SenderService(KafkaTemplate<String, Object> kafkaTemplate, @Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, Object> transactionalKafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.transactionalKafkaTemplate = transactionalKafkaTemplate;
    }

    public void send(Topic topic, Object payload) {
        send(kafkaTemplate, topic, payload);
    }

    /**
     * Use this method if you don't have started Kafka transaction.
     */
    @SafeVarargs
    public final void sendInLocalTransaction(Pair<Topic, Object>... messages) {
        transactionalKafkaTemplate.executeInTransaction(t -> {
            Arrays.stream(messages).forEach(m -> send(t, m.getLeft(), m.getRight()));
            return null;
        });
    }

    /**
     * Use this method if you have started Kafka transaction.
     */
    @SafeVarargs
    public final void sendTransactional(Pair<Topic, Object>... messages) {
        Arrays.stream(messages).forEach(m -> send(transactionalKafkaTemplate, m.getLeft(), m.getRight()));
    }

    private void send(KafkaOperations<String, Object> kafkaTemplate, Topic topic, Object payload) {
        List<Header> headers = new LinkedList<>();
        headers.add(new RecordHeader(KafkaHeaders.CORRELATION_ID, RequestAttributesUtil.getCorrelationId().getBytes()));
        headers.add(new RecordHeader(KafkaHeaders.TOPIC, topic.getTopicName().getBytes()));

        ProducerRecord<String, Object> producerRecord = new ProducerRecord<>(topic.getTopicName(), null, null, null, payload, headers);

        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(producerRecord);

        future.addCallback(new ListenableFutureCallback<>() {
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