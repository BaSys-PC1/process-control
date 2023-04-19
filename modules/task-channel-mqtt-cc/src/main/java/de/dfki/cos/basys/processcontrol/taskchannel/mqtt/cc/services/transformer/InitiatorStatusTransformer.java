package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

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
