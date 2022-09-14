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
public class ControlComponentRequestStatus extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = 6239160205502515344L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"ControlComponentRequestStatus\",\"namespace\":\"de.dfki.cos.basys.processcontrol.model\",\"fields\":[{\"name\":\"status\",\"type\":{\"type\":\"enum\",\"name\":\"RequestStatus\",\"symbols\":[\"UNDEFINED\",\"ACCEPTED\",\"REJECTED\",\"NOOP\",\"QUEUED\",\"DONE\",\"OK\",\"NOT_OK\"]}},{\"name\":\"message\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<ControlComponentRequestStatus> ENCODER =
      new BinaryMessageEncoder<ControlComponentRequestStatus>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<ControlComponentRequestStatus> DECODER =
      new BinaryMessageDecoder<ControlComponentRequestStatus>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<ControlComponentRequestStatus> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<ControlComponentRequestStatus> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<ControlComponentRequestStatus> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<ControlComponentRequestStatus>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this ControlComponentRequestStatus to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a ControlComponentRequestStatus from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a ControlComponentRequestStatus instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static ControlComponentRequestStatus fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private de.dfki.cos.basys.processcontrol.model.RequestStatus status;
  private java.lang.String message;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public ControlComponentRequestStatus() {}

  /**
   * All-args constructor.
   * @param status The new value for status
   * @param message The new value for message
   */
  public ControlComponentRequestStatus(de.dfki.cos.basys.processcontrol.model.RequestStatus status, java.lang.String message) {
    this.status = status;
    this.message = message;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return status;
    case 1: return message;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: status = (de.dfki.cos.basys.processcontrol.model.RequestStatus)value$; break;
    case 1: message = value$ != null ? value$.toString() : null; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
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
   * Creates a new ControlComponentRequestStatus RecordBuilder.
   * @return A new ControlComponentRequestStatus RecordBuilder
   */
  public static de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder newBuilder() {
    return new de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder();
  }

  /**
   * Creates a new ControlComponentRequestStatus RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new ControlComponentRequestStatus RecordBuilder
   */
  public static de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder newBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder other) {
    if (other == null) {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder();
    } else {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder(other);
    }
  }

  /**
   * Creates a new ControlComponentRequestStatus RecordBuilder by copying an existing ControlComponentRequestStatus instance.
   * @param other The existing instance to copy.
   * @return A new ControlComponentRequestStatus RecordBuilder
   */
  public static de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder newBuilder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus other) {
    if (other == null) {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder();
    } else {
      return new de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder(other);
    }
  }

  /**
   * RecordBuilder for ControlComponentRequestStatus instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<ControlComponentRequestStatus>
    implements org.apache.avro.data.RecordBuilder<ControlComponentRequestStatus> {

    private de.dfki.cos.basys.processcontrol.model.RequestStatus status;
    private java.lang.String message;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.status)) {
        this.status = data().deepCopy(fields()[0].schema(), other.status);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.message)) {
        this.message = data().deepCopy(fields()[1].schema(), other.message);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing ControlComponentRequestStatus instance
     * @param other The existing instance to copy.
     */
    private Builder(de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.status)) {
        this.status = data().deepCopy(fields()[0].schema(), other.status);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.message)) {
        this.message = data().deepCopy(fields()[1].schema(), other.message);
        fieldSetFlags()[1] = true;
      }
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
    public de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder setStatus(de.dfki.cos.basys.processcontrol.model.RequestStatus value) {
      validate(fields()[0], value);
      this.status = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'status' field has been set.
      * @return True if the 'status' field has been set, false otherwise.
      */
    public boolean hasStatus() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'status' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder clearStatus() {
      status = null;
      fieldSetFlags()[0] = false;
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
    public de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder setMessage(java.lang.String value) {
      validate(fields()[1], value);
      this.message = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'message' field has been set.
      * @return True if the 'message' field has been set, false otherwise.
      */
    public boolean hasMessage() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'message' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.processcontrol.model.ControlComponentRequestStatus.Builder clearMessage() {
      message = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ControlComponentRequestStatus build() {
      try {
        ControlComponentRequestStatus record = new ControlComponentRequestStatus();
        record.status = fieldSetFlags()[0] ? this.status : (de.dfki.cos.basys.processcontrol.model.RequestStatus) defaultValue(fields()[0]);
        record.message = fieldSetFlags()[1] ? this.message : (java.lang.String) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<ControlComponentRequestStatus>
    WRITER$ = (org.apache.avro.io.DatumWriter<ControlComponentRequestStatus>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<ControlComponentRequestStatus>
    READER$ = (org.apache.avro.io.DatumReader<ControlComponentRequestStatus>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeEnum(this.status.ordinal());

    out.writeString(this.message);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.status = de.dfki.cos.basys.processcontrol.model.RequestStatus.values()[in.readEnum()];

      this.message = in.readString();

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.status = de.dfki.cos.basys.processcontrol.model.RequestStatus.values()[in.readEnum()];
          break;

        case 1:
          this.message = in.readString();
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










