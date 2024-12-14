package com.example.userservice.app.kafka.config;

import com.example.userservice.app.kafka.dto.ApprovedRegisterUserDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ApprovedRegisterUserConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrap;

    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaConsumerGroupId;

    @Bean
    public ConsumerFactory<String, ApprovedRegisterUserDto> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        JsonDeserializer<ApprovedRegisterUserDto> deserializer = new JsonDeserializer<>(ApprovedRegisterUserDto.class);
        deserializer.setRemoveTypeHeaders(false);
        deserializer.addTrustedPackages("*");
        deserializer.setUseTypeMapperForKey(true);

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroupId);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, ApprovedRegisterUserDto>> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ApprovedRegisterUserDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}
