package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class EstopButtonStatusTransformer implements Function<String, EStopStatusStamped> {

    @Override
    public EStopStatusStamped apply(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.from(formatter.parse(jsonObject.getAsJsonPrimitive("timestamp").getAsString()));

        EStopStatusStamped status = EStopStatusStamped.newBuilder()
                .setTimestamp(TimestampUnix.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNseconds(instant.getNano())
                        .build())
                .setData(EStopStatus.newBuilder()
                        .setStatus(jsonObject.getAsJsonPrimitive("status").getAsBoolean() ? 1 : 0)
                        .build())
                .build();

        return status;
    }

}
