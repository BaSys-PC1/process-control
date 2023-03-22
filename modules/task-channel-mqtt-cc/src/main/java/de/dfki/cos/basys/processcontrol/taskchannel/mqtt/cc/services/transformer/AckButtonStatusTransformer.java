package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import de.dfki.cos.mrk40.avro.AckButtonStatusStamped;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class AckButtonStatusTransformer implements Function<String, AckButtonStatusStamped> {

    @Override
    public AckButtonStatusStamped apply(String s) {
        return null;
    }
}

