/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package de.dfki.cos.basys.processcontrol.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class ControlComponentResponse extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 3857923912821934590L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ControlComponentResponse\",\"namespace\":\"de.dfki.cos.basys.processcontrol.model\",\"fields\":[{\"name\":\"componentId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"aasId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"correlationId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"status\",\"type\":{\"type\":\"enum\",\"name\":\"RequestStatus\",\"symbols\":[\"UNDEFINED\",\"ACCEPTED\",\"REJECTED\",\"NOOP\",\"QUEUED\",\"DONE\",\"OK\",\"NOT_OK\"]}},{\"name\":\"statusCode\",\"type\":\"int\"},{\"name\":\"message\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"request\",\"type\":{\"type\":\"record\",\"name\":\"ControlComponentRequest\",\"fields\":[{\"name\":\"componentId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"aasId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"correlationId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"occupierId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"requestType\",\"type\":{\"type\":\"enum\",\"name\":\"ControlComponentRequestType\",\"symbols\":[\"OCCUPATION_COMMAND_REQUEST\",\"EXECUTION_MODE_REQUEST\",\"EXECUTION_COMMAND_REQUEST\",\"OPERATION_MODE_REQUEST\"]}},{\"name\":\"command\",\"type\":[{\"type\":\"enum\",\"name\":\"OccupationCommand\",\"symbols\":[\"FREE\",\"OCCUPY\",\"PRIO\"]},{\"type\":\"enum\",\"name\":\"ExecutionMode\",\"symbols\":[\"AUTO\",\"SEMIAUTO\",\"MANUAL\",\"SIMULATE\"]},{\"type\":\"enum\",\"name\":\"ExecutionCommand\",\"symbols\":[\"RESET\",\"START\",\"STOP\",\"HOLD\",\"UNHOLD\",\"SUSPEND\",\"UNSUSPEND\",\"ABORT\",\"CLEAR\"]},{\"type\":\"record\",\"name\":\"OperationMode\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"inputParameters\",\"type\":{\"type\":\"array\",\"items\":{\"type\":\"record\",\"name\":\"Variable\",\"fields\":[{\"name\":\"name\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"value\",\"type\":[\"null\",\"boolean\",\"long\",\"double\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"VariableType\",\"symbols\":[\"NULL\",\"BOOLEAN\",\"INTEGER\",\"STRING\",\"DOUBLE\",\"LONG\",\"DATE\"]}}]}},\"default\":[]},{\"name\":\"outputParameters\",\"type\":{\"type\":\"array\",\"items\":\"Variable\"},\"default\":[]}]}]}]}},{\"name\":\"outputParameters\",\"type\":{\"type\":\"array\",\"items\":\"Variable\"},\"default\":[]}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ControlComponentResponse> ENCODER =
      new BinaryMessageEncoder<ControlComponentResponse>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ControlComponentResponse> DECODER =
      new BinaryMessageDecoder<ControlComponentResponse>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ControlComponentResponse> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ControlComponentResponse> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ControlComponentResponse> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<ControlComponentResponse>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ControlComponentResponse to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ControlComponentResponse from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ControlComponentResponse instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ControlComponentResponse fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.String componentId;
   private java.lang.String aasId;
   private java.lang.String correlationId;
   private de.dfki.cos.basys.processcontrol.model.RequestStatus status;
   private int statusCode;
   private java.lang.String message;
   private de.dfki.cos.basys.processcontrol.model.ControlComponentRequest request;
   private java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> outputParameters;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ControlComponentResponse() {}

  /**
   * All-args constructor.
   * @param componentId The new value for componentId
   * @param aasId The new value for aasId
   * @param correlationId The new value for correlationId
   * @param status The new value for status
   * @param statusCode The new value for statusCode
   * @param message The new value for message
   * @param request The new value for request
   * @param outputParameters The new value for outputParameters
   */
  public ControlComponentResponse(java.lang.String componentId, java.lang.String aasId, java.lang.String correlationId, de.dfki.cos.basys.processcontrol.model.RequestStatus status, java.lang.Integer statusCode, java.lang.String message, de.dfki.cos.basys.processcontrol.model.ControlComponentRequest request, java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> outputParameters) {
    this.componentId = componentId;
    this.aasId = aasId;
    this.correlationId = correlationId;
    this.status = status;
    this.statusCode = statusCode;
    this.message = message;
    this.request = request;
    this.outputParameters = outputParameters;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return componentId;
    case 1: return aasId;
    case 2: return correlationId;
    case 3: return status;
    case 4: return statusCode;
    case 5: return message;
    case 6: return request;
    case 7: return outputParameters;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: componentId = value$ != null ? value$.toString() : null; break;
    case 1: aasId = value$ != null ? value$.toString() : null; break;
    case 2: correlationId = value$ != null ? value$.toString() : null; break;
    case 3: status = (de.dfki.cos.basys.processcontrol.model.RequestStatus)value$; break;
    case 4: statusCode = (java.lang.Integer)value$; break;
    case 5: message = value$ != null ? value$.toString() : null; break;
    case 6: request = (de.dfki.cos.basys.processcontrol.model.ControlComponentRequest)value$; break;
    case 7: outputParameters = (java.util.List<de.dfki.cos.basys.processcontrol.model.Variable>)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'componentId' field.
   * @return The value of the 'componentId' field.
   */
  public java.lang.String getComponentId() {
    return componentId;
  }


  /**
   * Sets the value of the 'componentId' field.
   * @param value the value to set.
   */
  public void setComponentId(java.lang.String value) {
    this.componentId = value;
  }

  /**
   * Gets the value of the 'aasId' field.
   * @return The value of the 'aasId' field.
   */
  public java.lang.String getAasId() {
    return aasId;
  }


  /**
   * Sets the value of the 'aasId' field.
   * @param value the value to set.
   */
  public void setAasId(java.lang.String value) {
    this.aasId = value;
  }

  /**
   * Gets the value of the 'correlationId' field.
   * @return The value of the 'correlationId' field.
   */
  public java.lang.String getCorrelationId() {
    return correlationId;
  }


  /**
   * Sets the value of the 'correlationId' field.
   * @param value the value to set.
   */
  public void setCorrelationId(java.lang.String value) {
    this.correlationId = value;
  }

  /**
   * Gets the value of the 'status' field.
   * @return The value of the 'status' field.
   */
  public de.dfki.cos.basys.processcontrol.model.RequestStatus getStatus() {
    return status;
  }


  /**
   * Sets the value of the 'status' field.
   * @param value the value to set.
   */
  public void setStatus(de.dfki.cos.basys.processcontrol.model.RequestStatus value) {
    this.status = value;
  }

  /**
   * Gets the value of the 'statusCode' field.
   * @return The value of the 'statusCode' field.
   */
  public int getStatusCode() {
    return statusCode;
  }


  /**
   * Sets the value of the 'statusCode' field.
   * @param value the value to set.
   */
  public void setStatusCode(int value) {
    this.statusCode = value;
  }

  /**
   * Gets the value of the 'message' field.
   * @return The value of the 'message' field.
   */
  public java.lang.String getMessage() {
    return message;
  }


  /**
   * Sets the value of the 'message' field.
   * @param value the value to set.
   */
  public void setMessage(java.lang.String value) {
    this.message = value;
  }

  /**
   * Gets the value of the 'request' field.
   * @return The value of the 'request' field.
   */
  public de.dfki.cos.basys.processcontrol.model.ControlComponentRequest getRequest() {
    return request;
  }


  /**
   * Sets the value of the 'request' field.
   * @param value the value to set.
   */
  public void setRequest(de.dfki.cos.basys.processcontrol.model.ControlComponentRequest value) {
    this.request = value;
  }

  /**
   * Gets the value of the 'outputParameters' field.
   * @return The value of the 'outputParameters' field.
   */
  public java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> getOutputParameters() {
    return outputParameters;
  }


  /**
   * Sets the value of the 'outputParameters' field.
   * @param value the value to set.
   */
  public void setOutputParameters(java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> value) {
    this.outputParameters = value;
  }

  /**
   * Creates a new ControlComponentResponse RecordBuilder.
   * @return A new ControlComponentResponse RecordBuilder
   */
  public static de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder newBuilder() {
    return new de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder();
  }

  /**
   * Creates a new ControlComponentResponse RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ControlComponentResponse RecordBuilder
   */
  public static de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder newBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder other) {
    if (other == null) {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder();
    } else {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder(other);
    }
  }

  /**
   * Creates a new ControlComponentResponse RecordBuilder by copying an existing ControlComponentResponse instance.
   * @param other The existing instance to copy.
   * @return A new ControlComponentResponse RecordBuilder
   */
  public static de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder newBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentResponse other) {
    if (other == null) {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder();
    } else {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder(other);
    }
  }

  /**
   * RecordBuilder for ControlComponentResponse instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ControlComponentResponse>
    implements org.apache.avro.data.RecordBuilder<ControlComponentResponse> {

    private java.lang.String componentId;
    private java.lang.String aasId;
    private java.lang.String correlationId;
    private de.dfki.cos.basys.processcontrol.model.RequestStatus status;
    private int statusCode;
    private java.lang.String message;
    private de.dfki.cos.basys.processcontrol.model.ControlComponentRequest request;
    private de.dfki.cos.basys.processcontrol.model.ControlComponentRequest.Builder requestBuilder;
    private java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> outputParameters;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.componentId)) {
        this.componentId = data().deepCopy(fields()[0].schema(), other.componentId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.aasId)) {
        this.aasId = data().deepCopy(fields()[1].schema(), other.aasId);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
      if (isValidValue(fields()[2], other.correlationId)) {
        this.correlationId = data().deepCopy(fields()[2].schema(), other.correlationId);
        fieldSetFlags()[2] = other.fieldSetFlags()[2];
      }
      if (isValidValue(fields()[3], other.status)) {
        this.status = data().deepCopy(fields()[3].schema(), other.status);
        fieldSetFlags()[3] = other.fieldSetFlags()[3];
      }
      if (isValidValue(fields()[4], other.statusCode)) {
        this.statusCode = data().deepCopy(fields()[4].schema(), other.statusCode);
        fieldSetFlags()[4] = other.fieldSetFlags()[4];
      }
      if (isValidValue(fields()[5], other.message)) {
        this.message = data().deepCopy(fields()[5].schema(), other.message);
        fieldSetFlags()[5] = other.fieldSetFlags()[5];
      }
      if (isValidValue(fields()[6], other.request)) {
        this.request = data().deepCopy(fields()[6].schema(), other.request);
        fieldSetFlags()[6] = other.fieldSetFlags()[6];
      }
      if (other.hasRequestBuilder()) {
        this.requestBuilder = de.dfki.cos.basys.processcontrol.model.ControlComponentRequest.newBuilder(other.getRequestBuilder());
      }
      if (isValidValue(fields()[7], other.outputParameters)) {
        this.outputParameters = data().deepCopy(fields()[7].schema(), other.outputParameters);
        fieldSetFlags()[7] = other.fieldSetFlags()[7];
      }
    }

    /**
     * Creates a Builder by copying an existing ControlComponentResponse instance
     * @param other The existing instance to copy.
     */
    private Builder(de.dfki.cos.basys.processcontrol.model.ControlComponentResponse other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.componentId)) {
        this.componentId = data().deepCopy(fields()[0].schema(), other.componentId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.aasId)) {
        this.aasId = data().deepCopy(fields()[1].schema(), other.aasId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.correlationId)) {
        this.correlationId = data().deepCopy(fields()[2].schema(), other.correlationId);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.status)) {
        this.status = data().deepCopy(fields()[3].schema(), other.status);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.statusCode)) {
        this.statusCode = data().deepCopy(fields()[4].schema(), other.statusCode);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.message)) {
        this.message = data().deepCopy(fields()[5].schema(), other.message);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.request)) {
        this.request = data().deepCopy(fields()[6].schema(), other.request);
        fieldSetFlags()[6] = true;
      }
      this.requestBuilder = null;
      if (isValidValue(fields()[7], other.outputParameters)) {
        this.outputParameters = data().deepCopy(fields()[7].schema(), other.outputParameters);
        fieldSetFlags()[7] = true;
      }
    }

    /**
      * Gets the value of the 'componentId' field.
      * @return The value.
      */
    public java.lang.String getComponentId() {
      return componentId;
    }


    /**
      * Sets the value of the 'componentId' field.
      * @param value The value of 'componentId'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setComponentId(java.lang.String value) {
      validate(fields()[0], value);
      this.componentId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'componentId' field has been set.
      * @return True if the 'componentId' field has been set, false otherwise.
      */
    public boolean hasComponentId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'componentId' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearComponentId() {
      componentId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'aasId' field.
      * @return The value.
      */
    public java.lang.String getAasId() {
      return aasId;
    }


    /**
      * Sets the value of the 'aasId' field.
      * @param value The value of 'aasId'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setAasId(java.lang.String value) {
      validate(fields()[1], value);
      this.aasId = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'aasId' field has been set.
      * @return True if the 'aasId' field has been set, false otherwise.
      */
    public boolean hasAasId() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'aasId' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearAasId() {
      aasId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /**
      * Gets the value of the 'correlationId' field.
      * @return The value.
      */
    public java.lang.String getCorrelationId() {
      return correlationId;
    }


    /**
      * Sets the value of the 'correlationId' field.
      * @param value The value of 'correlationId'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setCorrelationId(java.lang.String value) {
      validate(fields()[2], value);
      this.correlationId = value;
      fieldSetFlags()[2] = true;
      return this;
    }

    /**
      * Checks whether the 'correlationId' field has been set.
      * @return True if the 'correlationId' field has been set, false otherwise.
      */
    public boolean hasCorrelationId() {
      return fieldSetFlags()[2];
    }


    /**
      * Clears the value of the 'correlationId' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearCorrelationId() {
      correlationId = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /**
      * Gets the value of the 'status' field.
      * @return The value.
      */
    public de.dfki.cos.basys.processcontrol.model.RequestStatus getStatus() {
      return status;
    }


    /**
      * Sets the value of the 'status' field.
      * @param value The value of 'status'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setStatus(de.dfki.cos.basys.processcontrol.model.RequestStatus value) {
      validate(fields()[3], value);
      this.status = value;
      fieldSetFlags()[3] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[3];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearStatus() {
      status = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /**
      * Gets the value of the 'statusCode' field.
      * @return The value.
      */
    public int getStatusCode() {
      return statusCode;
    }


    /**
      * Sets the value of the 'statusCode' field.
      * @param value The value of 'statusCode'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setStatusCode(int value) {
      validate(fields()[4], value);
      this.statusCode = value;
      fieldSetFlags()[4] = true;
      return this;
    }

    /**
      * Checks whether the 'statusCode' field has been set.
      * @return True if the 'statusCode' field has been set, false otherwise.
      */
    public boolean hasStatusCode() {
      return fieldSetFlags()[4];
    }


    /**
      * Clears the value of the 'statusCode' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearStatusCode() {
      fieldSetFlags()[4] = false;
      return this;
    }

    /**
      * Gets the value of the 'message' field.
      * @return The value.
      */
    public java.lang.String getMessage() {
      return message;
    }


    /**
      * Sets the value of the 'message' field.
      * @param value The value of 'message'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setMessage(java.lang.String value) {
      validate(fields()[5], value);
      this.message = value;
      fieldSetFlags()[5] = true;
      return this;
    }

    /**
      * Checks whether the 'message' field has been set.
      * @return True if the 'message' field has been set, false otherwise.
      */
    public boolean hasMessage() {
      return fieldSetFlags()[5];
    }


    /**
      * Clears the value of the 'message' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearMessage() {
      message = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /**
      * Gets the value of the 'request' field.
      * @return The value.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentRequest getRequest() {
      return request;
    }


    /**
      * Sets the value of the 'request' field.
      * @param value The value of 'request'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setRequest(de.dfki.cos.basys.processcontrol.model.ControlComponentRequest value) {
      validate(fields()[6], value);
      this.requestBuilder = null;
      this.request = value;
      fieldSetFlags()[6] = true;
      return this;
    }

    /**
      * Checks whether the 'request' field has been set.
      * @return True if the 'request' field has been set, false otherwise.
      */
    public boolean hasRequest() {
      return fieldSetFlags()[6];
    }

    /**
     * Gets the Builder instance for the 'request' field and creates one if it doesn't exist yet.
     * @return This builder.
     */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentRequest.Builder getRequestBuilder() {
      if (requestBuilder == null) {
        if (hasRequest()) {
          setRequestBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequest.newBuilder(request));
        } else {
          setRequestBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequest.newBuilder());
        }
      }
      return requestBuilder;
    }

    /**
     * Sets the Builder instance for the 'request' field
     * @param value The builder instance that must be set.
     * @return This builder.
     */

    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setRequestBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequest.Builder value) {
      clearRequest();
      requestBuilder = value;
      return this;
    }

    /**
     * Checks whether the 'request' field has an active Builder instance
     * @return True if the 'request' field has an active Builder instance
     */
    public boolean hasRequestBuilder() {
      return requestBuilder != null;
    }

    /**
      * Clears the value of the 'request' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearRequest() {
      request = null;
      requestBuilder = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    /**
      * Gets the value of the 'outputParameters' field.
      * @return The value.
      */
    public java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> getOutputParameters() {
      return outputParameters;
    }


    /**
      * Sets the value of the 'outputParameters' field.
      * @param value The value of 'outputParameters'.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder setOutputParameters(java.util.List<de.dfki.cos.basys.processcontrol.model.Variable> value) {
      validate(fields()[7], value);
      this.outputParameters = value;
      fieldSetFlags()[7] = true;
      return this;
    }

    /**
      * Checks whether the 'outputParameters' field has been set.
      * @return True if the 'outputParameters' field has been set, false otherwise.
      */
    public boolean hasOutputParameters() {
      return fieldSetFlags()[7];
    }


    /**
      * Clears the value of the 'outputParameters' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentResponse.Builder clearOutputParameters() {
      outputParameters = null;
      fieldSetFlags()[7] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ControlComponentResponse build() {
      try {
        ControlComponentResponse record = new ControlComponentResponse();
        record.componentId = fieldSetFlags()[0] ? this.componentId : (java.lang.String) defaultValue(fields()[0]);
        record.aasId = fieldSetFlags()[1] ? this.aasId : (java.lang.String) defaultValue(fields()[1]);
        record.correlationId = fieldSetFlags()[2] ? this.correlationId : (java.lang.String) defaultValue(fields()[2]);
        record.status = fieldSetFlags()[3] ? this.status : (de.dfki.cos.basys.processcontrol.model.RequestStatus) defaultValue(fields()[3]);
        record.statusCode = fieldSetFlags()[4] ? this.statusCode : (java.lang.Integer) defaultValue(fields()[4]);
        record.message = fieldSetFlags()[5] ? this.message : (java.lang.String) defaultValue(fields()[5]);
        if (requestBuilder != null) {
          try {
            record.request = this.requestBuilder.build();
          } catch (org.apache.avro.AvroMissingFieldException e) {
            e.addParentField(record.getSchema().getField("request"));
            throw e;
          }
        } else {
          record.request = fieldSetFlags()[6] ? this.request : (de.dfki.cos.basys.processcontrol.model.ControlComponentRequest) defaultValue(fields()[6]);
        }
        record.outputParameters = fieldSetFlags()[7] ? this.outputParameters : (java.util.List<de.dfki.cos.basys.processcontrol.model.Variable>) defaultValue(fields()[7]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ControlComponentResponse>
    WRITER$ = (org.apache.avro.io.DatumWriter<ControlComponentResponse>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ControlComponentResponse>
    READER$ = (org.apache.avro.io.DatumReader<ControlComponentResponse>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

}










