package com.iliankm.sbms.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliankm.sbms.service.SenderService;
import com.iliankm.sbms.utils.AppProperties;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@Configuration
public class KafkaProducerConfig {

    private final AppProperties applicationProperties;
    private final ObjectMapper objectMapper;
    
    public KafkaProducerConfig(AppProperties applicationProperties, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    @Primary
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);
        return new DefaultKafkaProducerFactory<>(producerConfigs(), null, jsonSerializer);
    }

    @Bean
    public ProducerFactory<String, Object> transactionalProducerFactory() {
        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);
        DefaultKafkaProducerFactory<String, Object> producerFactory = new DefaultKafkaProducerFactory<>(transactionalProducerConfigs(), null, jsonSerializer);
        producerFactory.setTransactionIdPrefix(applicationProperties.applicationName());
        return producerFactory;
    }

    @Bean
    public KafkaTransactionManager<String, Object> kafkaTransactionManager(@Qualifier("transactionalProducerFactory") ProducerFactory<String, Object> transactionalProducerFactory) {
        return new KafkaTransactionManager<>(transactionalProducerFactory);
    }

    @Primary
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public KafkaTemplate<String, Object> transactionalKafkaTemplate(@Qualifier("transactionalProducerFactory") ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public SenderService kafkaSenderService(KafkaTemplate<String, Object> kafkaTemplate, @Qualifier("transactionalKafkaTemplate") KafkaTemplate<String, Object> transactionalKafkaTemplate) {
        return new SenderService(kafkaTemplate, transactionalKafkaTemplate);
    }

    private Map<String, Object> producerConfigs() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.kafkaBootstrapServers(),
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    }

    private Map<String, Object> transactionalProducerConfigs() {
        Map<String, Object> props = new HashMap<>(producerConfigs());

        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "transaction-id");

        return Collections.unmodifiableMap(props);
    }
}
