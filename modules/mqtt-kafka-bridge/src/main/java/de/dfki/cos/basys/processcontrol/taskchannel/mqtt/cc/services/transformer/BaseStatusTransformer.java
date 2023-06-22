package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.TimestampUnix;

public abstract class  BaseStatusTransformer <T> implements Function<String, T> {
	

	@Value("${basys.mqtt-to-kafka-bridge.useMessageTimestamp:true}")
	private boolean useMessageTimestamp;
 	
    @Override
    public final T apply(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

        Instant instant = getTimestamp(jsonObject);

        TimestampUnix ts = TimestampUnix.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNseconds(instant.getNano())
                .build();

        return applyWithInstant(jsonObject, ts);
    }

    private Instant getTimestamp(JsonObject jsonObject) {
        if (useMessageTimestamp) {
        	DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
            String dateAsString = jsonObject.getAsJsonPrimitive("timestamp").getAsString();
            return Instant.from(formatter.parse(dateAsString)); 	
        } else {
        	return Instant.now();
        }
	}

	protected abstract T applyWithInstant(JsonObject json, TimestampUnix ts);
}
