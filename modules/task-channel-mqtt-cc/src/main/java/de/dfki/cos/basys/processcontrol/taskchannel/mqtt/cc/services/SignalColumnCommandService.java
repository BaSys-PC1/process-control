package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services;

import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.SignalColumnStatusStamped;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Consumer;

@Service
@Slf4j
public class SignalColumnCommandService {

    @Autowired
    private IMqttClient mqttClient;

    @Value("${spring.cloud.stream.bindings.commandSignalColumn-in-0.destination}")
    private String COMMAND_TOPIC;

    protected void handleCommandSignalColumn(SignalColumnStatusStamped command) {
        log.info("new signal column command arrived: {}", command.getData().getYellow());

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.GERMAN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("timestamp",dateFormat.format(new Date(command.getTimestamp().getSeconds()*1000)));
        jsonObject.addProperty("status",command.getData().getYellow() == 0 ? "false" : "true");

        String topic = COMMAND_TOPIC.replace(".","/").replace("status","command");

        try {
            publish(topic, jsonObject.toString(), 2, false);
        } catch (MqttException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Bean
    public Consumer<SignalColumnStatusStamped> commandSignalColumn() {
        return this::handleCommandSignalColumn;
    }

    private void publish(final String topic, final String payload, int qos, boolean retained)
            throws MqttPersistenceException, MqttException {
        log.debug("publish to topic : " + topic);
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retained);

        mqttClient.publish(topic, mqttMessage);
    }

}
