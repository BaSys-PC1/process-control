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

