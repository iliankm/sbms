package com.iliankm.sbms.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iliankm.sbms.service.KafkaSenderService;
import com.iliankm.sbms.utils.ApplicationProperties;

@Configuration
public class KafkaProducerConfig {

    private final ApplicationProperties applicationProperties;

    private final ObjectMapper objectMapper;
    
    public KafkaProducerConfig(ApplicationProperties applicationProperties, ObjectMapper objectMapper) {
        this.applicationProperties = applicationProperties;
        this.objectMapper = objectMapper;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {

        JsonSerializer<Object> jsonSerializer = new JsonSerializer<>(objectMapper);

        return new DefaultKafkaProducerFactory<>(producerConfigs(), null, jsonSerializer);
    }

    @Bean
    public Map<String, Object> producerConfigs() {

        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, applicationProperties.kafkaBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    
    @Bean
    public KafkaSenderService kafkaSenderService() {
        return new KafkaSenderService(kafkaTemplate());
    }
    
}
