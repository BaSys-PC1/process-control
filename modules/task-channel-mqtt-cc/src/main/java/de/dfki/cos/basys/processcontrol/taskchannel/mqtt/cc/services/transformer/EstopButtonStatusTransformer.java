package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import de.dfki.cos.mrk40.avro.EStopStatusStamped;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class EstopButtonStatusTransformer implements Function<String, EStopStatusStamped> {

    @Override
    public EStopStatusStamped apply(String s) {
        return null;
    }
}
