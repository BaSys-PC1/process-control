package de.dfki.cos.basys.processcontrol.taskchannel.camunda.cc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CamundaCcTaskChannelApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamundaCcTaskChannelApplication.class, args);
	}

}
