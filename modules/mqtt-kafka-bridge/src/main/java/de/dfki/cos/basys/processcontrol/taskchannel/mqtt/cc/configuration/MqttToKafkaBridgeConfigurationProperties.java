package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration;

import lombok.Data;
import java.util.Map;


@Data
public class MqttToKafkaBridgeConfigurationProperties {

    private Map<String, MqttToKafkaRouteConfigurationProperties> routes;
}
