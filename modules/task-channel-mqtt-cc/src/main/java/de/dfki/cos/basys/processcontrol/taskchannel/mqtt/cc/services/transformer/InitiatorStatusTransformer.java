package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.InitiatorStatus;
import de.dfki.cos.mrk40.avro.InitiatorStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;

@Service
public class InitiatorStatusTransformer extends BaseStatusTransformer<InitiatorStatusStamped>{

    @Override
    protected InitiatorStatusStamped applyWithInstant(JsonObject jsonObject, TimestampUnix ts) {
        InitiatorStatusStamped status = InitiatorStatusStamped.newBuilder()
                .setTimestamp(ts)
                .setData(InitiatorStatus.newBuilder()
                        .setOccupied(jsonObject.getAsJsonPrimitive("status").getAsBoolean())
                        .build())
                .build();

        return status;
    }
}
