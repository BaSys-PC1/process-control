package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.ControlComponentRequest;
import de.dfki.cos.basys.processcontrol.model.ControlComponentResponse;
import de.dfki.cos.basys.processcontrol.model.OperationMode;
import de.dfki.cos.basys.processcontrol.model.Variable;
import org.apache.avro.specific.SpecificData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.avro.Schema;
@Configuration
public class JacksonConfiguration {

    abstract class IgnoreSchemaProperty
    {
        // You have to use the correct package for JsonIgnore,
        // fasterxml or codehaus
        @JsonIgnore abstract void getSchema();
        @JsonIgnore abstract void getSpecificData();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

        mapper.addMixIn(ControlComponentResponse.class, IgnoreSchemaProperty.class);
        mapper.addMixIn(ControlComponentRequest.class, IgnoreSchemaProperty.class);
        mapper.addMixIn(OperationMode.class, IgnoreSchemaProperty.class);
        mapper.addMixIn(Variable.class, IgnoreSchemaProperty.class);
        return mapper;
    }
}