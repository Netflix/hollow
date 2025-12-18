# Hollow Protocol Buffers Adapter

The Hollow Protocol Buffers Adapter provides functionality to populate a `HollowWriteStateEngine` from data encoded in Protocol Buffers format.

## Overview

This adapter allows you to convert Protocol Buffer messages into Hollow's in-memory data structures, enabling efficient serialization, deserialization, and data distribution using Hollow.

## Quick Start

The simplest way to use the Proto Adapter is with `HollowMessageMapper`, which works just like `HollowObjectMapper` for POJOs:

```java
// Create the mapper
HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
HollowMessageMapper mapper = new HollowMessageMapper(stateEngine);

// Add messages - schemas are automatically inferred
int ordinal = mapper.add(myProtoMessage);

// That's it! The mapper automatically:
// 1. Infers schemas from the message descriptor
// 2. Creates all nested type schemas
// 3. Writes the message to Hollow
```

## Detailed Usage

### 1. Define Your Protocol Buffer Schema

Create a `.proto` file defining your message structure:

```protobuf
syntax = "proto3";

package example;

message Person {
  int32 id = 1;
  string name = 2;
  string email = 3;
  repeated string phone_numbers = 4;
}
```

### 2. Compile the Protocol Buffer

Compile your `.proto` file using `protoc`:

```bash
protoc --java_out=src/main/java example.proto
```

### 3. Process Protocol Buffer Messages

Use `HollowMessageMapper` for automatic schema inference:

```java
HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
HollowMessageMapper mapper = new HollowMessageMapper(stateEngine);

// Create and process a Protocol Buffer message
Example.Person person = Example.Person.newBuilder()
    .setId(123)
    .setName("John Doe")
    .setEmail("john@example.com")
    .addPhoneNumbers("555-1234")
    .addPhoneNumbers("555-5678")
    .build();

// Schemas are automatically inferred and message is written
int ordinal = mapper.add(person);
```

### 4. Use the Hollow Data

After processing messages, prepare the state engine and use the data:

```java
// Prepare for reading
stateEngine.prepareForWrite();

// Round-trip to a read state engine
HollowReadStateEngine readStateEngine = new HollowReadStateEngine();
StateEngineRoundTripper.roundTripSnapshot(stateEngine, readStateEngine);

// Access the data
HollowObjectTypeReadState personState =
    (HollowObjectTypeReadState) readStateEngine.getTypeState("Person");

int id = personState.readInt(ordinal, 0);
String name = personState.readString(ordinal, 1);
String email = personState.readString(ordinal, 2);
```

## Supported Protocol Buffer Types

The adapter supports mapping between Protocol Buffer types and Hollow types:

| Protocol Buffer Type | Hollow Type | Notes |
|---------------------|-------------|-------|
| `int32`, `sint32`, `sfixed32` | `INT` | |
| `int64`, `sint64`, `sfixed64` | `LONG` | |
| `float` | `FLOAT` | |
| `double` | `DOUBLE` | |
| `bool` | `BOOLEAN` | |
| `string` | `STRING` | |
| `bytes` | `STRING` | Converted using UTF-8 |
| `enum` | `STRING` | Stored as string representation |
| `repeated` fields | `LIST` or `SET` | Mapped to Hollow collection types |
| nested `message` | `REFERENCE` | Mapped to referenced Hollow type |

## Field Processors

You can add custom field processors to handle specific fields with special logic:

```java
adapter.addFieldProcessor(new FieldProcessor() {
    @Override
    public String getEntityName() {
        return "Person";
    }

    @Override
    public String getFieldName() {
        return "email";
    }

    @Override
    public void processField(Message message, HollowWriteStateEngine stateEngine,
                           HollowObjectWriteRecord writeRecord) {
        // Custom processing logic
        String email = ((Example.Person) message).getEmail();
        String normalizedEmail = email.toLowerCase();
        writeRecord.setString("email", normalizedEmail);
    }
});
```

## Ignoring List Ordering

For repeated fields that represent unordered collections (tags, IDs), you can enable global list deduplication:

```java
HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
HollowMessageMapper mapper = new HollowMessageMapper(stateEngine);
mapper.ignoreListOrdering(); // Treat all repeated fields as unordered

// Now ["a", "b", "c"] and ["c", "a", "b"] are considered identical
int ordinal = mapper.add(myMessage);
```

**Benefits:**
- **Memory savings**: 20-40% reduction for tag/ID-heavy data
- **Better deduplication**: Lists with same elements in different orders share storage

**When to use:**
- ✅ Tags, categories, IDs (order doesn't matter)
- ✅ Unordered collections

**When NOT to use:**
- ❌ Event sequences, histories (order matters)
- ❌ Ordered data

Similar to `HollowObjectMapper.ignoreListOrdering()`.

## Primary Keys

Define primary keys using the `hollow_primary_key` message option:

```protobuf
import "hollow_options.proto";

message Person {
  option (com.netflix.hollow.hollow_primary_key) = "id";

  int32 id = 1;
  string name = 2;
  string email = 3;
}

// Composite primary key
message Account {
  option (com.netflix.hollow.hollow_primary_key) = "account_id";
  option (com.netflix.hollow.hollow_primary_key) = "region";

  int32 account_id = 1;
  string region = 2;
  string data = 3;
}
```

Extract primary keys from messages:

```java
HollowMessageMapper mapper = new HollowMessageMapper(stateEngine);
RecordPrimaryKey key = mapper.extractPrimaryKey(myMessage);
// key contains only the fields specified in hollow_primary_key option
```

If no `hollow_primary_key` option is specified, all scalar (non-repeated, non-message) fields are used as the primary key.

## Field Path Remapping

Remap field paths if your Hollow schema structure differs from your Protocol Buffer structure:

```java
// Remap a nested field to a top-level field in Hollow
adapter.remapFieldPath("Person", "email", "contact", "email");
```

## Current Limitations

### File-Based Processing

File-based Protocol Buffer processing is not currently implemented. Use `processMessage()` directly with individual parsed Protocol Buffer `Message` objects.

Protocol Buffer files require specific framing/delimiting, and implementations vary. Common approaches include:
- Length-delimited messages using `writeDelimitedTo()` / `parseDelimitedFrom()`
- Size-prefixed messages
- Custom framing protocols

### Top-Level Collections and Maps

Top-level collection and map schemas are not commonly used with Protocol Buffers and are not currently supported. Instead:
- Wrap collections in a message with a repeated field
- Wrap maps in a message with a map field

Nested collections and repeated fields within messages are fully supported.

## Comparison with Other Adapters

Hollow provides adapters for different data formats:

- **`hollow-jsonadapter`**: For JSON data (text-based, human-readable)
- **`hollow-zenoadapter`**: For Netflix Zeno format
- **`hollow-protoadapter`**: For Protocol Buffers (binary, compact, strongly-typed)

Use the Protocol Buffers adapter when:
- You already have Protocol Buffer definitions
- You need compact binary serialization
- You want strong schema validation at compile-time
- You're integrating with systems that use Protocol Buffers

## Dependencies

```gradle
dependencies {
    implementation 'com.netflix.hollow:hollow-protoadapter:x.y.z'
    implementation 'com.google.protobuf:protobuf-java:3.21.12'
}
```

The `hollow_options.proto` file is included in the JAR at `proto/hollow_options.proto` and can be imported in your proto files using:

```protobuf
import "hollow_options.proto";
```

**Available options:**
- `hollow_primary_key` (message option): Define primary key fields
- `hollow_ignore_list_ordering` (field option): Reserved for future per-field control

**Note**: Currently, `ignoreListOrdering()` applies globally to all repeated fields. The field-level option is defined for future use but not yet implemented.

Make sure to configure your `protoc` with `--proto_path` pointing to the JAR resources:

```bash
protoc --proto_path=/path/to/hollow-protoadapter.jar/proto \
       --proto_path=. \
       --java_out=out \
       your_message.proto
```

## Additional Resources

- [Hollow Documentation](https://github.com/Netflix/hollow)
- [Protocol Buffers Documentation](https://developers.google.com/protocol-buffers)
- [Example Usage](src/test/java/com/netflix/hollow/protoadapter/HollowProtoAdapterTest.java)
