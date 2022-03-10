package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class DeploymentServiceInitializer {

    @Autowired
    private DeploymentService deploymentService;

    @PostConstruct
    public void initializeDeploymentService() {
        deploymentService.startMonitoring();
    }
}
