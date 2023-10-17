package de.dfki.cos.basys.processcontrol.wgsmanagementservice.services;

import de.dfki.cos.basys.processcontrol.model.Notification;
import de.dfki.cos.basys.processcontrol.model.StepChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@Slf4j
public class KafkaService {

    @Autowired
    private StreamBridge streamBridge;

    @Bean
    public Consumer<Notification> notificationUpdates() {
        return this::handleNotificationUpdates;
    }

    private void handleNotificationUpdates(Notification notification) {
        log.info("new notification of type {} and show {}", notification.getType(), notification.getShow());

//        if (notification.getShow()) {
//            notification.setShow(false);
//            streamBridge.send("notification", notification);
//        }

    }

    @Bean
    public Consumer<StepChange> stepChangeUpdates() {
        return this::handleStepChangeUpdates;
    }

    private void handleStepChangeUpdates(StepChange stepChange) {
        log.info("new step change with id {}", stepChange.getWorkstepId());
    }
}
