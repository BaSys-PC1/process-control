package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration.MqttToKafkaBridgeConfigurationProperties;
import de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration.MqttToKafkaRouteConfigurationProperties;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.function.Function;

@Service
@Slf4j
public class MqttToKafkaBridgeService implements ApplicationContextAware {

    @Autowired
    private IMqttClient mqttClient;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MqttToKafkaBridgeConfigurationProperties config;

    @PostConstruct
    public void postConstruct() {
        config.getRoutes().forEach((binding, route) -> {
            log.info("creating route {}: {}", binding, route.getSource());
            try {
                mqttClient.subscribe(route.getSource(), (s, mqttMessage) -> {
                    log.debug("message arrived on topic {}: {}", s, mqttMessage.toString() );
                    Function<String, Object> transformer = applicationContext.getBean(route.getTransformerService(), Function.class );
                    log.debug("transforming message with {}", transformer.getClass());
                    Object result = transformer.apply( mqttMessage.toString());
                    streamBridge.send(binding, result);
                });
            } catch (MqttException e) {
                log.error("ERROR", e);
                throw new RuntimeException(e);
            }
        });

    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
