package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Service
@Slf4j
public class MqttTaskChannelService implements IMqttMessageListener {

    @Autowired
    private IMqttClient mqttClient;

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private ObjectMapper mapper;

    private Map<CharSequence, ControlComponentRequest> issuedRequests = new HashMap<>();

    @Value("${spring.cloud.stream.bindings.controlComponentRequests.destination}")
    private String REQUEST_TOPIC;
    @Value("${spring.cloud.stream.bindings.controlComponentResponses-in-0.destination}")
    private String RESPONSE_TOPIC;

    @PostConstruct
    public void postConstruct() {
        try {
            mqttClient.subscribe(REQUEST_TOPIC, this);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        try {
            ControlComponentRequest request = mapper.readValue(mqttMessage.getPayload(), ControlComponentRequest.class);
            if (request != null) {
                issuedRequests.put(request.getCorrelationId(), request);
                log.info("new request arrived for: {}, correlationId: {}", request.getComponentId(), request.getCorrelationId());
                streamBridge.send("controlComponentRequests", request);
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void handleComponentResponse(ControlComponentResponse response) {
        log.info("new response arrived from: {}, correlationId: {}", response.getComponentId(), response.getCorrelationId());
        ControlComponentRequest externalTask = issuedRequests.remove(response.getRequest().getCorrelationId());
        if (externalTask != null) {
            try {
                String payload  = mapper.writeValueAsString(response);
                publish(RESPONSE_TOPIC, payload, 1, false);
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            } catch (MqttPersistenceException e) {
                log.error(e.getMessage(), e);
            } catch (MqttException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            //do nothing; this worker instance has not issued that control component request
        }
    }

    @Bean
    public Consumer<ControlComponentResponse> controlComponentResponses() {
        return this::handleComponentResponse;
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
