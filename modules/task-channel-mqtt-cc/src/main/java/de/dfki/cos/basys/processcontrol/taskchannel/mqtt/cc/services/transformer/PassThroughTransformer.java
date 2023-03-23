package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PassThroughTransformer implements Function<String, String> {

    @Override
    public String apply(String s) {
        return s;
    }
}
