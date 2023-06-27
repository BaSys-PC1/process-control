package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.services.transformer;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

import com.google.gson.JsonPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import de.dfki.cos.mrk40.avro.TimestampUnix;

@Slf4j
public abstract class  BaseStatusTransformer <T> implements Function<String, T> {
	

	@Value("${basys.mqtt-to-kafka-bridge.useMessageTimestamp:true}")
	private boolean useMessageTimestamp;
 	
    @Override
    public final T apply(String json) {
        try {
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);

            Instant instant = getTimestamp(jsonObject);

            TimestampUnix ts = TimestampUnix.newBuilder()
                    .setSeconds(instant.getEpochSecond())
                    .setNseconds(instant.getNano())
                    .build();

            return applyWithInstant(jsonObject, ts);
        } catch (RuntimeException e) {
           log.error(e.getMessage());
           return null;
        }
    }

    private Instant getTimestamp(JsonObject jsonObject) {
        if (useMessageTimestamp) {
        	DateTimeFormatter formatter = DateTimeFormatter.ISO_INSTANT;
            JsonPrimitive p = jsonObject.getAsJsonPrimitive("timestamp");
            if (p != null) {
                String dateAsString = p.getAsString();
                return Instant.from(formatter.parse(dateAsString));
            } else {
                log.warn("message does not contain a timestamp. falling back to Instant.now()");
                return Instant.now();
            }
        } else {
        	return Instant.now();
        }
	}

	protected abstract T applyWithInstant(JsonObject json, TimestampUnix ts);
}
