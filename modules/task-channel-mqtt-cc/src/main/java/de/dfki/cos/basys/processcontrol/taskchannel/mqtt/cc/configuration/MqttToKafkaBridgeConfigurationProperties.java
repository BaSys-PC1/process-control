package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration;

import lombok.Data;
import java.util.Map;


@Data
public class MqttToKafkaBridgeConfigurationProperties {

    private String name;
    private Map<String, MqttToKafkaRouteConfigurationProperties> bridges;
}
