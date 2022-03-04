package de.dfki.cos.basys.platform.spring.kafkamessagingtest.config;

import org.springframework.cloud.stream.binder.kafka.BinderHeaderMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean("kafkaBinderHeaderMapper")
    public BinderHeaderMapper kafkaBinderHeaderMapper() {
        return new BinderHeaderMapper();
    }
}
