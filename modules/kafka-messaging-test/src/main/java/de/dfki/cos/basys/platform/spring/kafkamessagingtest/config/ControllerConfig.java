package de.dfki.cos.basys.platform.spring.kafkamessagingtest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;

@Configuration
public class ControllerConfig {

    @Bean
    public ConfigurableWebBindingInitializer getInit() {
        return new ConfigurableWebBindingInitializer();
    }

}
