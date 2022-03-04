package de.dfki.cos.basys.platform.spring.kafkamessagingtest.service;

import de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Slf4j
public class MessageConsumerService {

    @Bean
    public Consumer<TestMessage> logTestMessage() {
        return testMessage -> log.info(testMessage.toString());
    }
}
