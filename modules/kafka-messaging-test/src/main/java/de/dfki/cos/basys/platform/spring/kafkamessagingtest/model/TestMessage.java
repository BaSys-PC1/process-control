/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package de.dfki.cos.basys.platform.spring.kafkamessagingtest.model;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class TestMessage extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -7251865766212537145L;
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"TestMessage\",\"namespace\":\"de.dfki.cos.basys.platform.spring.kafkamessagingtest.model\",\"fields\":[{\"name\":\"message\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<TestMessage> ENCODER =
      new BinaryMessageEncoder<TestMessage>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<TestMessage> DECODER =
      new BinaryMessageDecoder<TestMessage>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<TestMessage> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<TestMessage> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<TestMessage> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<TestMessage>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this TestMessage to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a TestMessage from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a TestMessage instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static TestMessage fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

   private java.lang.CharSequence message;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public TestMessage() {}

  /**
   * All-args constructor.
   * @param message The new value for message
   */
  public TestMessage(java.lang.CharSequence message) {
    this.message = message;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return message;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: message = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'message' field.
   * @return The value of the 'message' field.
   */
  public java.lang.CharSequence getMessage() {
    return message;
  }


  /**
   * Sets the value of the 'message' field.
   * @param value the value to set.
   */
  public void setMessage(java.lang.CharSequence value) {
    this.message = value;
  }

  /**
   * Creates a new TestMessage RecordBuilder.
   * @return A new TestMessage RecordBuilder
   */
  public static de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder newBuilder() {
    return new de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder();
  }

  /**
   * Creates a new TestMessage RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new TestMessage RecordBuilder
   */
  public static de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder newBuilder(de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder other) {
    if (other == null) {
      return new de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder();
    } else {
      return new de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder(other);
    }
  }

  /**
   * Creates a new TestMessage RecordBuilder by copying an existing TestMessage instance.
   * @param other The existing instance to copy.
   * @return A new TestMessage RecordBuilder
   */
  public static de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder newBuilder(de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage other) {
    if (other == null) {
      return new de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder();
    } else {
      return new de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder(other);
    }
  }

  /**
   * RecordBuilder for TestMessage instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<TestMessage>
    implements org.apache.avro.data.RecordBuilder<TestMessage> {

    private java.lang.CharSequence message;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.message)) {
        this.message = data().deepCopy(fields()[0].schema(), other.message);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
    }

    /**
     * Creates a Builder by copying an existing TestMessage instance
     * @param other The existing instance to copy.
     */
    private Builder(de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage other) {
      super(SCHEMA$);
      if (isValidValue(fields()[0], other.message)) {
        this.message = data().deepCopy(fields()[0].schema(), other.message);
        fieldSetFlags()[0] = true;
      }
    }

    /**
      * Gets the value of the 'message' field.
      * @return The value.
      */
    public java.lang.CharSequence getMessage() {
      return message;
    }


    /**
      * Sets the value of the 'message' field.
      * @param value The value of 'message'.
      * @return This builder.
      */
    public de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder setMessage(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.message = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'message' field has been set.
      * @return True if the 'message' field has been set, false otherwise.
      */
    public boolean hasMessage() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'message' field.
      * @return This builder.
      */
    public de.dfki.cos.basys.platform.spring.kafkamessagingtest.model.TestMessage.Builder clearMessage() {
      message = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public TestMessage build() {
      try {
        TestMessage record = new TestMessage();
        record.message = fieldSetFlags()[0] ? this.message : (java.lang.CharSequence) defaultValue(fields()[0]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<TestMessage>
    WRITER$ = (org.apache.avro.io.DatumWriter<TestMessage>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<TestMessage>
    READER$ = (org.apache.avro.io.DatumReader<TestMessage>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.message);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.message = in.readString(this.message instanceof Utf8 ? (Utf8)this.message : null);

    } else {
      for (int i = 0; i < 1; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.message = in.readString(this.message instanceof Utf8 ? (Utf8)this.message : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}










