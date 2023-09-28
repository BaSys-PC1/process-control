package de.dfki.cos.basys.processcontrol.scalemanagementservice.services;

import de.dfki.cos.basys.processcontrol.scalemanagementservice.model.InstanceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.json.Json;
import javax.json.JsonObject;
import java.util.concurrent.TimeUnit;

/**
 * Send configuration (retrieved from AAS) to scale-control as soon as it comes online.
 */

@Service
@Slf4j
@EnableScheduling
public class ServiceRegistryManager {

    private final RestTemplate restTemplate;

    private boolean isUp;

    @Autowired
    private IMqttClient mqttClient;

    @Value("${eureka.client.serviceUrl.defaultZone:http://localhost:8761/eureka}")
    private String eurekaConnectionString;

    public ServiceRegistryManager(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
        this.isUp = false;
    }

    @Scheduled(fixedRate = 5000)
    public void checkAvailability() {
        String url = this.eurekaConnectionString + "/v2/apps/scale-controller/scale-pc";
        InstanceWrapper instance = null;

        try {
            instance = this.restTemplate.getForObject(url, InstanceWrapper.class);
        }
        catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() != HttpStatus.NOT_FOUND) {
                throw ex;
            }
        }

        if (instance == null) return;

        if (instance.getInstance().getStatus().equals("UP") && !this.isUp) {
            this.isUp = true;
            this.sendConfig();
        }
        else if (instance.getInstance().getStatus().equals("DOWN") && this.isUp) {
            this.isUp = false;
        }
    }

    public void sendConfig() {
        // TODO: Retrieve from AAS
        JsonObject tarePayload = Json.createObjectBuilder()
                .add("channel", 1)
                .add("value", 0.043855) // box weight
                .build();
        JsonObject refPiecesPayload = Json.createObjectBuilder()
                .add("channel", 1)
                .add("pieces", 6)// 0.0014 / piece with High precision
                .build();
        try {
            TimeUnit.SECONDS.sleep(1);
            mqttClient.publish("scale/startMeasurement", new MqttMessage("{}".getBytes()));
            TimeUnit.SECONDS.sleep(1);
            mqttClient.publish("scale/tare", new MqttMessage(tarePayload.toString().getBytes()));
            TimeUnit.SECONDS.sleep(1);
            mqttClient.publish("scale/refPieces", new MqttMessage(refPiecesPayload.toString().getBytes()));
        } catch (MqttException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
