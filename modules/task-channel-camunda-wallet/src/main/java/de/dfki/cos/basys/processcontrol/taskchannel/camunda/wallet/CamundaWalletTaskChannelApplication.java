package de.dfki.cos.basys.processcontrol.taskchannel.camunda.wallet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CamundaWalletTaskChannelApplication {

	public static void main(String[] args) {
		SpringApplication.run(CamundaWalletTaskChannelApplication.class, args);
	}

}
