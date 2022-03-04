package de.dfki.cos.basys.platform.spring.kafkamessagingtest.service;

import de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.JsonMessage;
import de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class MessageProducerService {

    @Autowired
    private StreamBridge streamBridge;

    public void sendMessage(String message) {

        //JsonMessage jsonMessage = new JsonMessage(message);
        //streamBridge.send("messageProducerJson", jsonMessage);

        TestMessage avroMessage = TestMessage.newBuilder().setMessage(message).build();
        streamBridge.send("messageProducerAvro", avroMessage);

    }
}
