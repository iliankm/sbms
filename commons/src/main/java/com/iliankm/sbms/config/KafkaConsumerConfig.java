package com.iliankm.sbms.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliankm.sbms.utils.AppProperties;
import org.springframework.kafka.transaction.KafkaTransactionManager;

@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    private final AppProperties applicationProperties;
    private final ObjectMapper objectMapper;

    public KafkaConsumerConfig(AppProperties applicationProperties, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    @Primary
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(Object.class, objectMapper);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), null, jsonDeserializer);
    }

    @Bean
    public ConsumerFactory<String, Object> transactionalConsumerFactory() {
        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(Object.class, objectMapper);
        jsonDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(transactionalConsumerConfigs(), null, jsonDeserializer);
    }

    @Primary
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> kafkaListenerContainerFactory(ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Object>> transactionalKafkaListenerContainerFactory(@Qualifier("transactionalConsumerFactory") ConsumerFactory<String, Object> consumerFactory, KafkaTransactionManager<String, Object> kafkaTransactionManager) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setTransactionManager(kafkaTransactionManager);

        return factory;
    }

    private Map<String, Object> consumerConfigs() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.kafkaBootstrapServers(),
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.GROUP_ID_CONFIG, applicationProperties.kafkaGroupId(),
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, applicationProperties.kafkaAutoOffsetReset());
    }

    private Map<String, Object> transactionalConsumerConfigs() {
        Map<String, Object> props = new HashMap<>(consumerConfigs());

        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");

        return Collections.unmodifiableMap(props);
     }
}
