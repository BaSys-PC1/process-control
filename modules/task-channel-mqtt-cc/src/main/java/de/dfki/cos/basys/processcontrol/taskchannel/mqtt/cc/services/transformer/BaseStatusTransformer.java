package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.dfki.cos.mrk40.avro.AckButtonStatus;
import de.dfki.cos.mrk40.avro.AckButtonStatusStamped;
import de.dfki.cos.mrk40.avro.TimestampUnix;

import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class  BaseStatusTransformer <T> implements Function<String, T> {
    @Override
    public final T apply(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
        String dateAsString = jsonObject.getAsJsonPrimitive("timestamp").getAsString();
        Instant instant = Instant.from(formatter.parse(dateAsString));
        //instant = instant.minus(Duration.of(2, ChronoUnit.HOURS));

        TimestampUnix ts = TimestampUnix.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNseconds(instant.getNano())
                .build();

        return applyWithInstant(jsonObject, ts);
    }

    protected abstract T applyWithInstant(JsonObject json, TimestampUnix ts);
}
