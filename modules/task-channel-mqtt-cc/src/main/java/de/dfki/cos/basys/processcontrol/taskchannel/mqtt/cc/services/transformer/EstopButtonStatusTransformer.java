package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.EStopStatus;
import de.dfki.cos.mrk40.avro.EStopStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;

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
