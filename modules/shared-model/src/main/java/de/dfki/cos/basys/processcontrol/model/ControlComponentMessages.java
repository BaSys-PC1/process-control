/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package de.dfki.cos.basys.processcontrol.model;

@org.apache.avro.specific.AvroGenerated
public interface ControlComponentMessages {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"ControlComponentMessages\",\"namespace\":\"de.dfki.cos.basys.processcontrol.model\",\"types\":[{\"type\":\"enum\",\"name\":\"ExecutionCommand\",\"symbols\":[\"RESET\",\"START\",\"STOP\",\"HOLD\",\"UNHOLD\",\"SUSPEND\",\"UNSUSPEND\",\"ABORT\",\"CLEAR\"]},{\"type\":\"enum\",\"name\":\"ExecutionMode\",\"symbols\":[\"AUTO\",\"SEMIAUTO\",\"MANUAL\",\"SIMULATE\"]},{\"type\":\"enum\",\"name\":\"OccupationCommand\",\"symbols\":[\"FREE\",\"OCCUPY\",\"PRIO\"]},{\"type\":\"enum\",\"name\":\"VariableType\",\"symbols\":[\"NULL\",\"BOOLEAN\",\"INTEGER\",\"STRING\",\"DOUBLE\",\"LONG\",\"DATE\"]},{\"type\":\"enum\",\"name\":\"ControlComponentRequestType\",\"symbols\":[\"OCCUPATION_COMMAND_REQUEST\",\"EXECUTION_MODE_REQUEST\",\"EXECUTION_COMMAND_REQUEST\",\"OPERATION_MODE_REQUEST\"]},{\"type\":\"record\",\"name\":\"Variable\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"value\",\"type\":[\"null\",\"boolean\",\"long\",\"double\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"type\",\"type\":\"VariableType\"}]},{\"type\":\"record\",\"name\":\"OperationMode\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"inputParameters\",\"type\":{\"type\":\"array\",\"items\":\"Variable\"},\"default\":[]},{\"name\":\"outputParameters\",\"type\":{\"type\":\"array\",\"items\":\"Variable\"},\"default\":[]}]},{\"type\":\"record\",\"name\":\"ControlComponentRequest\",\"fields\":[{\"name\":\"componentId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"aasId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"correlationId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"occupierId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"requestType\",\"type\":\"ControlComponentRequestType\"},{\"name\":\"command\",\"type\":[\"OccupationCommand\",\"ExecutionMode\",\"ExecutionCommand\",\"OperationMode\"]}]},{\"type\":\"enum\",\"name\":\"RequestStatus\",\"symbols\":[\"UNDEFINED\",\"ACCEPTED\",\"REJECTED\",\"NOOP\",\"QUEUED\",\"DONE\",\"OK\",\"NOT_OK\"]},{\"type\":\"record\",\"name\":\"ControlComponentRequestStatus\",\"fields\":[{\"name\":\"status\",\"type\":\"RequestStatus\"},{\"name\":\"message\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]},{\"type\":\"record\",\"name\":\"ControlComponentResponse\",\"fields\":[{\"name\":\"componentId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"aasId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"correlationId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"status\",\"type\":\"RequestStatus\"},{\"name\":\"statusCode\",\"type\":\"int\"},{\"name\":\"message\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"request\",\"type\":\"ControlComponentRequest\"},{\"name\":\"outputParameters\",\"type\":{\"type\":\"array\",\"items\":\"Variable\"},\"default\":[]}]}],\"messages\":{}}");

  @org.apache.avro.specific.AvroGenerated
  public interface Callback extends ControlComponentMessages {
    public static final org.apache.avro.Protocol PROTOCOL = de.dfki.cos.basys.processcontrol.model.ControlComponentMessages.PROTOCOL;
  }
}