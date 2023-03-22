package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import de.dfki.cos.mrk40.avro.InitiatorStatusStamped;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class InitiatorStatusTransformer implements Function<String, InitiatorStatusStamped> {

    @Override
    public InitiatorStatusStamped apply(String s) {
        return null;
    }
}
