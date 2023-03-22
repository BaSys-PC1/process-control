package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import de.dfki.cos.mrk40.avro.LightCurtainStatusStamped;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SafetyLightCurtainStatusTransformer implements Function<String, LightCurtainStatusStamped> {

    @Override
    public LightCurtainStatusStamped apply(String s) {
        return null;
    }
}
