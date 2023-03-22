package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import de.dfki.cos.mrk40.avro.PressStatusStamped;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PressStatusTransformer implements Function<String, PressStatusStamped> {

    @Override
    public PressStatusStamped apply(String s) {
        return null;
    }
}
