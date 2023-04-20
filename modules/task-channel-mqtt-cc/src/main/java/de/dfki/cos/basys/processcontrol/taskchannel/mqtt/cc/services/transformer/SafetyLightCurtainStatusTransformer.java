package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import java.util.Collections;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.LightCurtainStatus;
import de.dfki.cos.mrk40.avro.LightCurtainStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;

@Service
public class SafetyLightCurtainStatusTransformer extends BaseStatusTransformer<LightCurtainStatusStamped> {

    @Override
    protected LightCurtainStatusStamped applyWithInstant(JsonObject jsonObject, TimestampUnix ts) {
        LightCurtainStatusStamped status = LightCurtainStatusStamped.newBuilder()
                .setTimestamp(ts)
                .setData(LightCurtainStatus.newBuilder()
                        .setStatus(Collections.singletonList(jsonObject.getAsJsonPrimitive("status").getAsBoolean()))
                        .build())
                .build();

        return status;
    }
}
