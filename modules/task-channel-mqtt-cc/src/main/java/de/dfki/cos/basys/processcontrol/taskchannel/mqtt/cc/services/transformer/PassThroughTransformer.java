package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import java.util.function.Function;

import org.springframework.stereotype.Service;

@Service
public class PassThroughTransformer implements Function<String, String> {

    @Override
    public String apply(String s) {
        return s;
    }
}
