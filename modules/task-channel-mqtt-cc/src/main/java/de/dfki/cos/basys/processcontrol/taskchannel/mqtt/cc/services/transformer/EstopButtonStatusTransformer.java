package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class EstopButtonStatusTransformer extends BaseStatusTransformer<EStopStatusStamped>{
    @Override
    protected EStopStatusStamped applyWithInstant(JsonObject jsonObject, TimestampUnix ts) {
        EStopStatusStamped status = EStopStatusStamped.newBuilder()
                .setTimestamp(ts)
                .setData(EStopStatus.newBuilder()
                        .setStatus(jsonObject.getAsJsonPrimitive("status").getAsBoolean() ? 1 : 0)
                        .build())
                .build();

        return status;
    }

}
