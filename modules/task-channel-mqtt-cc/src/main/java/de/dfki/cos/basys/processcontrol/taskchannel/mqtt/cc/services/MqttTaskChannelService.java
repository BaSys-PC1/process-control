package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.data.Json;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
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
            log.info("subscribe to MQTT topic: {}", REQUEST_TOPIC);
            mqttClient.subscribe(REQUEST_TOPIC, this);
        } catch (MqttException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) {
        if (log.isDebugEnabled()) {
            log.debug("message arrived on topic {}: {}", s, new String(mqttMessage.getPayload(), StandardCharsets.UTF_8));
        }
        try {
            ControlComponentRequest request = mapper.readValue(mqttMessage.getPayload(), ControlComponentRequest.class);
            if (request != null) {
                issuedRequests.put(request.getCorrelationId(), request);
                log.info("new request arrived for: {}, correlationId: {}", request.getComponentId(), request.getCorrelationId());
                if (log.isDebugEnabled()) {
                    log.debug(request.toString());
                }
                streamBridge.send("controlComponentRequests", request);
                log.debug("request forwarded to kafka");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    protected void handleComponentResponse(ControlComponentResponse response) {
        ControlComponentRequest externalTask = issuedRequests.remove(response.getRequest().getCorrelationId());
        if (externalTask != null) {
            log.info("new response arrived from: {}, correlationId: {}", response.getComponentId(), response.getCorrelationId());
            if (log.isDebugEnabled()) {
                log.debug(response.toString());
            }
            try {

//                DatumWriter<ControlComponentResponse> writer = new GenericDatumWriter<>(ControlComponentResponse.getClassSchema());
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                Encoder jsonEncoder = EncoderFactory.get().jsonEncoder(ControlComponentResponse.getClassSchema(), stream);
//                writer.write(response, jsonEncoder);
//                jsonEncoder.flush();
//                String payload = stream.toString(Charsets.UTF_8);

                //String payload = Json.toString(response);
                String payload  = mapper.writeValueAsString(response);
                publish(RESPONSE_TOPIC, payload, 2, false);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.info("unknown response arrived from: {}, correlationId: {}", response.getComponentId(), response.getCorrelationId());
            log.info("this worker instance has not issued that control component request");
        }
    }

    @Bean
    public Consumer<ControlComponentResponse> controlComponentResponses() {
        return this::handleComponentResponse;
    }

    private void publish(final String topic, final String payload, int qos, boolean retained) {
        if (log.isDebugEnabled()) {
            log.debug("publish to topic : " + topic);
            log.debug(payload);
        }
        MqttMessage mqttMessage = new MqttMessage();
        mqttMessage.setPayload(payload.getBytes());
        mqttMessage.setQos(qos);
        mqttMessage.setRetained(retained);

        try {
            mqttClient.publish(topic, mqttMessage);
        } catch (MqttException e) {
            log.error("could not publish message to MQTT topic {}: {}", topic, payload);
            log.error(e.getMessage(), e);
        }
    }

}
