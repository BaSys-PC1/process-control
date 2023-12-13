package de.dfki.cos.basys.processcontrol.wgsmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
//@EnableDiscoveryClient
public class WGSManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WGSManagementServiceApplication.class, args);
	}

}
