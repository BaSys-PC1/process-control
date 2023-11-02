package de.dfki.cos.basys.processcontrol.scalemanagementservice.services;

import de.dfki.cos.basys.processcontrol.scalemanagementservice.model.InstanceWrapper;
import de.dfki.cos.basys.processcontrol.scalemanagementservice.model.ScaleProps;
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
import java.util.HashMap;
import java.util.Map;
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
        try {
            mqttClient.publish("scale/startMeasurement", new MqttMessage("{}".getBytes()));
            TimeUnit.SECONDS.sleep(1);
        } catch (MqttException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        // TODO: Retrieve from AAS
        Map<Integer, ScaleProps> piecesPerScale = new HashMap<>();
        piecesPerScale.put(1, new ScaleProps(4, "Mutter", 0.058085, -0.012322));
        piecesPerScale.put(2, new ScaleProps(2, "Unterschale", 0.0548049, 0.01462));
        piecesPerScale.put(3, new ScaleProps(3, "Microcontroller", 0.0573, 0.022978));
        piecesPerScale.put(4, new ScaleProps(3, "Oberschale", 0.056996, 0.015253));
        piecesPerScale.put(5, new ScaleProps(4, "Schraube", 0.057252, 0.0003348)); //(0.0014 / piece with High precision)

        for (Map.Entry<Integer, ScaleProps> pair : piecesPerScale.entrySet()) {
            JsonObject tarePayload = Json.createObjectBuilder()
                    .add("channel", pair.getKey())
                    .add("value", pair.getValue().getTare()) // box weight
                    .build();
            JsonObject zeroDeviationPayload = Json.createObjectBuilder()
                    .add("channel", pair.getKey())
                    .add("zeroDeviation", pair.getValue().getZeroDeviation())
                    .build();
            JsonObject refPiecesPayload = Json.createObjectBuilder()
                    .add("channel", pair.getKey())
                    .add("pieces", pair.getValue().getPieces())
                    .add("material", pair.getValue().getMaterial())
                    .build();
            try {
                TimeUnit.SECONDS.sleep(1);
                mqttClient.publish("scale/zeroDeviation", new MqttMessage(zeroDeviationPayload.toString().getBytes()));
                TimeUnit.SECONDS.sleep(1);
                mqttClient.publish("scale/tare", new MqttMessage(tarePayload.toString().getBytes()));
                TimeUnit.SECONDS.sleep(1);
                mqttClient.publish("scale/refPieces", new MqttMessage(refPiecesPayload.toString().getBytes()));
                TimeUnit.SECONDS.sleep(1);
            } catch (MqttException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
