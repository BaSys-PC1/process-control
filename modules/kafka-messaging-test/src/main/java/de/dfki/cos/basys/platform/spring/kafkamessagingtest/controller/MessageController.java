package de.dfki.cos.basys.platform.spring.kafkamessagingtest.controller;

import de.dfki.cos.basys.platform.spring.kafkamessagingtest.service.MessageProducerService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    @Autowired
    private final MessageProducerService messagingService;

    @PostMapping("/{message}")
    public void sendMessage(@PathVariable String message) {
        log.info("Receive message: {}", message);
        messagingService.sendMessage(message);
    }
}
