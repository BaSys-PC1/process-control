package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.configuration;

import de.dfki.cos.basys.common.rest.camunda.api.DeploymentApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

@Slf4j
@Configuration
public class DeploymentServiceConfig {

    @Value("${camunda.processDeployer.endpoint}")
    private String camundaRestEndpoint;

    @Bean
    public DeploymentApi deploymentApi() {
        String endpoint = camundaRestEndpoint;
        if (endpoint.endsWith("/"))
            endpoint = endpoint.substring(0, endpoint.length()-1);

        DeploymentApi api = new DeploymentApi();
        api.getApiClient().setBasePath(endpoint);
        return api;
    }

}
