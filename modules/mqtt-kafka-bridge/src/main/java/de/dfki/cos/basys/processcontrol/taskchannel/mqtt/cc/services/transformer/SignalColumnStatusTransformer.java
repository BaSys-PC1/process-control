package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.SignalColumnStatus;
import de.dfki.cos.mrk40.avro.SignalColumnStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;

@Service
public class SignalColumnStatusTransformer extends BaseStatusTransformer<SignalColumnStatusStamped> {
    @Override
    protected SignalColumnStatusStamped applyWithInstant(JsonObject jsonObject, TimestampUnix ts) {
        SignalColumnStatusStamped status = SignalColumnStatusStamped.newBuilder()
                .setTimestamp(ts)
                .setData(SignalColumnStatus.newBuilder()
                        .setRed(jsonObject.getAsJsonPrimitive("red").getAsBoolean() ? 1 : 0)
                        .setYellow(jsonObject.getAsJsonPrimitive("yellow").getAsBoolean() ? 1 : 0)
                        .setGreen(jsonObject.getAsJsonPrimitive("green").getAsBoolean() ? 1 : 0)
                        .setWhite(jsonObject.getAsJsonPrimitive("white").getAsBoolean() ? 1 : 0)
                        .build())
                .build();

        return status;
    }
}
