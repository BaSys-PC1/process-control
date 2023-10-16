package de.dfki.cos.basys.processcontrol.wgsmanagementservice.services;

import de.dfki.cos.basys.aas.registry.events.RegistryEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class KafkaService {

    private StreamBridge streamBridge;

    @Bean
    public Consumer<RegistryEvent> aasRegistryUpdates() {
        return this::handleAasRegistryUpdates;
    }

    private void handleAasRegistryUpdates(RegistryEvent event) {
        log.info("new AAS registry event: " + event.getType());

        //streamBridge.send("aasRegistryUpdates", event);
    }
}
