package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration;

import lombok.Data;

@Data
public class MqttToKafkaRouteConfigurationProperties {
    private String source;
    private String transformerService;
}
