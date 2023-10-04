package de.dfki.cos.basys.processcontrol.wgsmanagementservice.services;

import de.dfki.cos.basys.processcontrol.wgsmanagementservice.model.registry.InstanceWrapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

/**
 * Check if WGS is online
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
        String url = this.eurekaConnectionString + "/v2/apps/wgs/wgs-pc";
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
        }
        else if (instance.getInstance().getStatus().equals("DOWN") && this.isUp) {
            this.isUp = false;
        }
    }

}
