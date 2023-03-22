package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import de.dfki.cos.mrk40.avro.SignalColumnStatusStamped;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class SignalColumnStatusTransformer implements Function<String, SignalColumnStatusStamped> {

    @Override
    public SignalColumnStatusStamped apply(String s) {
        return null;
    }
}
