package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class PressStatusTransformer implements Function<String, PressStatusStamped> {

    @Override
    public PressStatusStamped apply(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        Instant instant = Instant.from(formatter.parse(jsonObject.getAsJsonPrimitive("timestamp").getAsString()));

        PressStatusStamped status = PressStatusStamped.newBuilder()
                .setTimestamp(TimestampUnix.newBuilder()
                        .setSeconds(instant.getEpochSecond())
                        .build())
                .setData(PressStatus.newBuilder()
                        .setCounter(jsonObject.getAsJsonPrimitive("counter").getAsInt())
                        .setExState(ExecutionState.valueOf(jsonObject.getAsJsonPrimitive("state").getAsString().toUpperCase()))
                        .setOpMode(OperationMode.newBuilder()
                                .setName(jsonObject.getAsJsonPrimitive("opmode").getAsString())
                                .setShortName(jsonObject.getAsJsonPrimitive("opmode").getAsString())
                                .setDescription("-")
                                .setParameters("-")
                                .build())
                        .build())
                .build();

        return status;
    }
}
