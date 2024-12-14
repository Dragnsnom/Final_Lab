package com.example.userservice.app.kafka.config;

import com.example.userservice.app.kafka.dto.RegisterUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.JacksonUtils;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RegisterUserProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Bean
    public ObjectMapper objectMapper() {
        return JacksonUtils.enhancedObjectMapper();
    }

    @Bean
    public Map<String, Object> createRegisterUserProducer() {
        Map<String, Object> map = new HashMap<>();
        map.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        map.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        map.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        map.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        return map;
    }

    @Bean
    public ProducerFactory<String, RegisterUserDto> createRegisterUserProducerFactory(ObjectMapper mapper) {
        DefaultKafkaProducerFactory<String, RegisterUserDto> kafkaProducerFactory =
                new DefaultKafkaProducerFactory<>(createRegisterUserProducer());
        kafkaProducerFactory.setValueSerializer(new JsonSerializer<>(mapper));
        return kafkaProducerFactory;
    }

    @Bean
    public KafkaTemplate<String, RegisterUserDto> createRegisterUserProducerTemplate(
            ProducerFactory<String, RegisterUserDto> kafkaProducerFactory) {
        return new KafkaTemplate<>(kafkaProducerFactory);
    }
}
