package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.AckButtonStatus;
import de.dfki.cos.mrk40.avro.AckButtonStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;

@Service
public class AckButtonStatusTransformer extends BaseStatusTransformer<AckButtonStatusStamped> {
    @Override
    protected AckButtonStatusStamped applyWithInstant(JsonObject jsonObject, TimestampUnix ts) {
        AckButtonStatusStamped status = AckButtonStatusStamped.newBuilder()
                .setTimestamp(ts)
                .setData(AckButtonStatus.newBuilder()
                        .setStatus(jsonObject.getAsJsonPrimitive("status").getAsBoolean() ? 1 : 0)
                        .build())
                .build();

        return status;
    }
}

