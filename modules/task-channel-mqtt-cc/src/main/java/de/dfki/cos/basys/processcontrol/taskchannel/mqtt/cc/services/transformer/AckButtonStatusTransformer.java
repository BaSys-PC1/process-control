package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.AckButtonStatus;
import de.dfki.cos.mrk40.avro.AckButtonStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class AckButtonStatusTransformer implements Function<String, AckButtonStatusStamped> {

    @Override
    public AckButtonStatusStamped apply(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.from(formatter.parse(jsonObject.getAsJsonPrimitive("timestamp").getAsString()));

        AckButtonStatusStamped status = AckButtonStatusStamped.newBuilder()
                .setTimestamp(TimestampUnix.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .build())
                .setData(AckButtonStatus.newBuilder()
                        .setStatus(jsonObject.getAsJsonPrimitive("status").getAsBoolean() ? 1 : 0)
                        .build())
                .build();

        return status;
    }
}

