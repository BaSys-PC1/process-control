package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.function.Function;

@Service
public class SafetyLightCurtainStatusTransformer implements Function<String, LightCurtainStatusStamped> {

    @Override
    public LightCurtainStatusStamped apply(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.from(formatter.parse(jsonObject.getAsJsonPrimitive("timestamp").getAsString()));

        LightCurtainStatusStamped status = LightCurtainStatusStamped.newBuilder()
                .setTimestamp(TimestampUnix.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .setNseconds(instant.getNano())
                        .build())
                .setData(LightCurtainStatus.newBuilder()
                        .setStatus(Collections.singletonList(jsonObject.getAsJsonPrimitive("status").getAsBoolean()))
                        .build())
                .build();

        return status;
    }
}
