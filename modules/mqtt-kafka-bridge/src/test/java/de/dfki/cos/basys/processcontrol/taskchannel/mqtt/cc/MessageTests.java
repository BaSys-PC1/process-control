package de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.dfki.cos.basys.processcontrol.model.*;
import de.dfki.cos.basys.processcontrol.taskchannel.mqtt.cc.configuration.JacksonConfiguration;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.commons.compress.utils.Charsets;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MessageTests {

    private ControlComponentRequest ccRequest;

    private ControlComponentResponse ccResponse;

    @Before
    public void setup() {

        var inputs = new ArrayList<Variable>();
        inputs.add(Variable.newBuilder().setName("inputsBoolean").setType(VariableType.BOOLEAN).setValue(false).build());
        inputs.add(Variable.newBuilder().setName("inputsInt").setType(VariableType.INTEGER).setValue(40).build());
        inputs.add(Variable.newBuilder().setName("inputsLong").setType(VariableType.LONG).setValue(42l).build());
        inputs.add(Variable.newBuilder().setName("inputsDouble").setType(VariableType.DOUBLE).setValue(42.1).build());
        inputs.add(Variable.newBuilder().setName("inputsString").setType(VariableType.STRING).setValue("hello").build());

        var outputs = new ArrayList<Variable>();
        outputs.add(Variable.newBuilder().setName("outputsBoolean").setType(VariableType.BOOLEAN).setValue(null).build());
        outputs.add(Variable.newBuilder().setName("outputsInt").setType(VariableType.INTEGER).setValue(null).build());
        outputs.add(Variable.newBuilder().setName("outputsLong").setType(VariableType.LONG).setValue(null).build());
        outputs.add(Variable.newBuilder().setName("outputsDouble").setType(VariableType.DOUBLE).setValue(null).build());
        outputs.add(Variable.newBuilder().setName("outputsString").setType(VariableType.STRING).setValue(null).build());

        var opMode = OperationMode.newBuilder()
                .setName("testOpMode")
                .setInputParameters(inputs)
                .setOutputParameters(outputs)
                .build();

        ccRequest = ControlComponentRequest.newBuilder()
                .setAasId("testAasId")
                .setCorrelationId("testCorrelationId")
                .setComponentId("testComponentId")
                .setOccupierId("testOccupier")
                .setRequestType(ControlComponentRequestType.OPERATION_MODE_REQUEST)
                .setOperationMode(opMode)
                .setOccupationCommand(null)
                .setExecutionMode(null)
                .setExecutionCommand(null)
                .build();
    }

    @Test
    public void testOperationModeRequestSerdes() {
        DatumWriter<ControlComponentRequest> writer = new GenericDatumWriter<>(ControlComponentRequest.getClassSchema());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Encoder jsonEncoder = null;
        try {
            jsonEncoder = EncoderFactory.get().jsonEncoder(ControlComponentRequest.getClassSchema(), stream);
            writer.write(ccRequest, jsonEncoder);
            jsonEncoder.flush();
            String payload = stream.toString(Charsets.UTF_8);
            System.out.println(payload);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testJsonSerialization() {
        JacksonConfiguration config = new JacksonConfiguration();
        ObjectMapper mapper = config.objectMapper();
        try {
            String payload  = mapper.writeValueAsString(ccRequest);
            System.out.println(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
