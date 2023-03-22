package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqttToKafkaBridgeConfiguration {

    @ConfigurationProperties(prefix = "basys.mqtt-to-kafka-bridge")
    @Bean
    public MqttToKafkaBridgeConfigurationProperties properties() {
        return new MqttToKafkaBridgeConfigurationProperties();
    }

    @Bean
    public String mqttToKafkaBridge(MqttToKafkaBridgeConfigurationProperties config) {
        System.out.println("configure" + config.getBridges().size());
        return "";
    }
}
