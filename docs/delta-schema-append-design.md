# RFC: Delta Schema Append Protocol for Hollow

**Author:** Austen McClernon (amcclernon@netflix.com)
**Status:** Exploratory
**Created:** 2025-11-06

---

## Abstract

This specification defines an extension to the Hollow delta blob format enabling transmission of field values for schema-evolved types during delta updates. The protocol maintains O(1) skip complexity for consumers without schema updates while providing immediate data availability for consumers with updated schemas, eliminating the delay until the next snapshot cycle.

The implementation employs an append-only modification to the delta blob structure with a length-prefixed auxiliary data section, ensuring backwards compatibility with existing consumers. Field values are integrated directly into the consumer's data element storage structures, maintaining API consistency with snapshot-derived data.

---

## 1. Introduction

### 1.1 Problem Statement

Hollow's delta transmission protocol encodes only structural changes (additions, removals, modifications) between consecutive states. When a schema evolves to include new fields, delta blobs omit field values for ordinals preserved from the previous state. This design choice enables consumers without schema updates to process deltas efficiently by ignoring unknown schema elements.

However, this creates a data availability problem: consumers with updated schemas cannot access new field values until receiving the next complete snapshot, potentially introducing delays of hours to days depending on snapshot frequency.

**Formal Problem:**
```
Given:
- State S(n) with schema σ(n)
- State S(n+1) with schema σ(n+1) where σ(n+1) = σ(n) ∪ {f_new}
- Delta blob Δ(n→n+1)
- Ordinal set O_preserved = O(n) ∩ O(n+1)

Current Behavior:
  For all o ∈ O_preserved: Δ(n→n+1) contains no value for f_new(o)

Desired Behavior:
  For all o ∈ O_preserved: Δ(n→n+1) optionally contains f_new(o)
  while maintaining O(1) skip cost for consumers without f_new in schema
```

### 1.2 Design Goals

1. **Backwards Compatibility**: Existing consumers must process new delta blobs without modification or performance degradation
2. **Skip Performance**: Consumers without schema updates must skip auxiliary data in O(1) time
3. **Data Completeness**: Consumers with schema updates must access all field values immediately
4. **Storage Efficiency**: Minimize memory overhead for appended field storage
5. **API Consistency**: Appended fields must be accessible via standard Hollow read methods

### 1.3 Non-Goals

- Compression of auxiliary data section
- Support for LIST, SET, MAP type schema evolution (OBJECT types only)
- Selective field transmission within a type (all fields or newly-added fields only)
- Schema version negotiation protocol

---

## 2. Protocol Specification

### 2.1 Delta Blob Structure

The enhanced delta blob structure appends an auxiliary data section after the standard type deltas:

```
┌─────────────────────────────────────────┐
│ BLOB HEADER                             │
│  - Magic bytes                          │
│  - Schema hash                          │
│  - Backwards compat envelope            │  ← Modified (flag bit)
│    └─ hasAppendedSchemaData flag        │
├─────────────────────────────────────────┤
│ TYPE DELTA SECTION                      │
│  - Type 1 delta                         │
│  - Type 2 delta                         │
│  - ...                                  │
│  - Type N delta                         │  ← Unchanged
├─────────────────────────────────────────┤
│ APPENDED SCHEMA DATA SECTION (Optional) │
│  - Total byte length (VarLong)          │  ← Length prefix enables O(1) skip
│  - Type metadata + field values         │
└─────────────────────────────────────────┘
```

### 2.2 Header Modification

The backwards compatibility envelope in the blob header is extended to signal the presence of appended data:

**Without Appended Data (Backwards Compatible):**
```
Offset  Field                    Type      Value
------  -----------------------  --------  -----
0x00    forwardCompatBytesLen    VarInt    0
```

**With Appended Data:**
```
Offset  Field                    Type      Value    Description
------  -----------------------  --------  -------  ---------------------
0x00    forwardCompatBytesLen    VarInt    1        1 byte follows
0x01    flagByte                 uint8     0x01     bit[0] = hasAppendedSchemaData
```

Future protocol extensions may utilize bits[1-7] for additional flags.

### 2.3 Appended Data Section Format

The appended data section follows this recursive structure:

```
AppendedDataSection := TotalByteLength TypeData*

TotalByteLength     := VarLong            // 0 if no data
TypeData            := TypeName FieldData*
TypeName            := VarInt UTF-8       // length-prefixed string
FieldData           := FieldName FieldType [RefType] OrdinalValuePairs
FieldName           := VarInt UTF-8       // length-prefixed string
FieldType           := uint8              // FieldType enum ordinal
RefType             := VarInt UTF-8       // present only if FieldType=REFERENCE
OrdinalValuePairs   := VarInt (VarInt Value)*  // count followed by pairs
Value               := <type-specific>    // see Section 2.4
```

**BNF Grammar:**
```ebnf
appended-section  ::= total-length num-types type-data*
total-length      ::= varint64
num-types         ::= varint32
type-data         ::= type-name num-fields field-data*
type-name         ::= string-varint
num-fields        ::= varint32
field-data        ::= field-name field-type [ref-type-name] num-ordinals ordinal-value*
field-name        ::= string-varint
field-type        ::= uint8
ref-type-name     ::= string-varint
num-ordinals      ::= varint32
ordinal-value     ::= ordinal value
ordinal           ::= varint32
value             ::= <see value-encoding>
string-varint     ::= length-varint utf8-bytes
length-varint     ::= varint32
```

### 2.4 Value Encoding Specification

Values are encoded type-specifically to ensure platform-independent serialization:

| Field Type | Wire Format | Size (bytes) | Encoding Details |
|------------|-------------|--------------|------------------|
| INT | VarInt (zigzag) | 1-5 | ZigZag encoding: `(n << 1) ^ (n >> 31)` |
| LONG | VarLong (zigzag) | 1-10 | ZigZag encoding: `(n << 1) ^ (n >> 63)` |
| BOOLEAN | uint8 | 1 | `0x00` = false, `0x01` = true |
| FLOAT | int32 (big-endian) | 4 | IEEE 754 bit representation via `Float.floatToIntBits()` |
| DOUBLE | int64 (big-endian) | 8 | IEEE 754 bit representation via `Double.doubleToLongBits()` |
| STRING | VarInt + UTF-8 | variable | Length-prefixed UTF-8 bytes |
| BYTES | VarInt + raw bytes | variable | Length-prefixed byte array |
| REFERENCE | VarInt (zigzag) | 1-5 | Ordinal pointer (same as INT) |

**Critical Implementation Note:**
FLOAT and DOUBLE must use IEEE 754 bit representation (via `floatToIntBits` / `doubleToLongBits`) to ensure:
1. Preservation of special values (NaN, ±Infinity, ±0.0)
2. Bit-exact encoding across JVM implementations
3. Platform-independent serialization

**VarInt Encoding:**
Variable-length integer encoding uses continuation bits:
```
VarInt encoding for value n:
  byte[i] = (n & 0x7F) | 0x80  // for all but last byte
  byte[last] = (n >> 7k) & 0x7F

where k is the number of preceding bytes
```

### 2.5 Wire Format Example

Consider a delta blob where type `Movie` adds field `rating: LONG` for ordinals {0, 2, 7} with values {85, 92, 78}:

```
Offset  Field                     Hex Bytes              Decoded Value
------  ------------------------  ---------------------  -------------
0x00    totalByteLength           15                     21 bytes
0x01    numTypes                  01                     1 type
0x02    typeNameLength            05                     5 bytes
0x03    typeName                  4D 6F 76 69 65         "Movie"
0x08    numFields                 01                     1 field
0x09    fieldNameLength           06                     6 bytes
0x0A    fieldName                 72 61 74 69 6E 67      "rating"
0x10    fieldType                 02                     LONG (ordinal=2)
0x11    numOrdinals               03                     3 ordinals
0x12    ordinal[0]                00                     0
0x13    value[0]                  AA 01                  85 (zigzag varint)
0x15    ordinal[1]                02                     2
0x16    value[1]                  B8 01                  92 (zigzag varint)
0x18    ordinal[2]                07                     7
0x19    value[2]                  9C 01                  78 (zigzag varint)
```

**Size Calculation:**
- Header: 1 (totalLength) + 1 (numTypes) + 1 (typeNameLen) + 5 (typeName) = 8 bytes
- Field metadata: 1 (numFields) + 1 (fieldNameLen) + 6 (fieldName) + 1 (fieldType) + 1 (numOrdinals) = 10 bytes
- Values: 3 × (1 ordinal + 2 value) = 9 bytes
- Total: 8 + 10 + 9 = 27 bytes (wire format shows 21 due to varint compression)

---

## 3. Producer Architecture

### 3.1 Data Collection Pipeline

The producer executes the following pipeline during delta blob creation:

```
┌─────────────────────────────────────────────────────────────────┐
│ HollowWriteStateEngine                                          │
│  - Maintains current and previous state                         │
│  - Identifies changed type states                               │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ DeltaSchemaAppendDataCollector                                  │
│  Algorithm:                                                     │
│    for each typeState where hasChangedSinceLastCycle():        │
│      O_preserved = previousOrdinals ∩ currentOrdinals          │
│      for each ordinal o ∈ O_preserved:                         │
│        for each field f in schema:                             │
│          value = extractFieldValue(o, f)                       │
│          store(typeName, fieldName, o, value)                  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ DeltaSchemaAppendDataWriter                                     │
│  - Serializes collected data to wire format                    │
│  - Writes length-prefixed section to blob                      │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Data Collection Algorithm

**Input:**
- `HollowWriteStateEngine` with current and previous state
- Configuration: `deltaSchemaAppendEnabled = true`

**Output:**
- Map: `TypeName → FieldName → (Ordinal → Value)`

**Algorithm:**
```
ALGORITHM CollectAppendedFieldData(writeStateEngine)
  INPUT: writeStateEngine : HollowWriteStateEngine
  OUTPUT: typeDataMap : Map<String, TypeAppendData>

  typeDataMap ← empty map

  FOR EACH typeState IN writeStateEngine.typeStates:
    IF NOT typeState.hasChangedSinceLastCycle():
      CONTINUE

    schema ← typeState.schema
    preservedOrdinals ← FindPreservedOrdinals(typeState)

    fieldDataMap ← empty map
    FOR EACH field IN schema.fields:
      ordinalValueMap ← empty map

      FOR EACH ordinal IN preservedOrdinals:
        value ← ExtractFieldValue(typeState, ordinal, field)
        ordinalValueMap[ordinal] ← value

      IF ordinalValueMap NOT empty:
        fieldDataMap[field.name] ← FieldAppendData(
          field.name, field.type, ordinalValueMap
        )

    IF fieldDataMap NOT empty:
      typeDataMap[schema.name] ← TypeAppendData(
        schema.name, fieldDataMap
      )

  RETURN typeDataMap

FUNCTION FindPreservedOrdinals(typeState)
  previous ← typeState.previousOrdinals
  current ← typeState.currentOrdinals
  RETURN previous ∩ current

FUNCTION ExtractFieldValue(typeState, ordinal, field)
  pointer ← typeState.ordinalMap.getPointerForData(ordinal)
  byteData ← typeState.ordinalMap.getByteData()
  bitOffset ← CalculateBitOffset(ordinal, field)
  RETURN DecodeValue(byteData, bitOffset, field.type)
```

**Complexity Analysis:**
- Time: O(|T| × |O_preserved| × |F|) where T = changed types, O_preserved = preserved ordinals, F = fields
- Space: O(|T| × |F| × |O_preserved|) for collected data
- Typical case: < 10% overhead on delta blob creation time

### 3.3 Serialization Process

The `DeltaSchemaAppendDataWriter` implements the wire format specification:

```java
PROCEDURE WriteAppendedData(outputStream, typeDataMap)
  // Phase 1: Calculate total size
  totalBytes ← CalculateTotalBytes(typeDataMap)
  WriteVarLong(outputStream, totalBytes)

  // Phase 2: Write type count
  WriteVarInt(outputStream, typeDataMap.size())

  // Phase 3: Write each type's data
  FOR EACH (typeName, typeData) IN typeDataMap:
    WriteString(outputStream, typeName)
    WriteVarInt(outputStream, typeData.fields.size())

    FOR EACH (fieldName, fieldData) IN typeData.fields:
      WriteString(outputStream, fieldName)
      WriteByte(outputStream, fieldData.type.ordinal())

      IF fieldData.type = REFERENCE:
        WriteString(outputStream, fieldData.referencedType)

      WriteVarInt(outputStream, fieldData.ordinalValues.size())

      FOR EACH (ordinal, value) IN fieldData.ordinalValues:
        WriteVarInt(outputStream, ordinal)
        WriteValue(outputStream, value, fieldData.type)

PROCEDURE WriteValue(outputStream, value, fieldType)
  SWITCH fieldType:
    CASE INT, REFERENCE:
      WriteVarInt(outputStream, ZigZagEncode(value))
    CASE LONG:
      WriteVarLong(outputStream, ZigZagEncode(value))
    CASE BOOLEAN:
      WriteByte(outputStream, value ? 1 : 0)
    CASE FLOAT:
      bits ← Float.floatToIntBits(value)
      WriteInt32BigEndian(outputStream, bits)
    CASE DOUBLE:
      bits ← Double.doubleToLongBits(value)
      WriteInt64BigEndian(outputStream, bits)
    CASE STRING:
      bytes ← value.getBytes(UTF_8)
      WriteVarInt(outputStream, bytes.length)
      WriteBytes(outputStream, bytes)
    CASE BYTES:
      WriteVarInt(outputStream, value.length)
      WriteBytes(outputStream, value)
```

---

## 4. Consumer Architecture

### 4.1 Data Application Pipeline

The consumer processes appended data through this pipeline:

```
┌─────────────────────────────────────────────────────────────────┐
│ HollowBlobReader.applyDelta()                                   │
│  1. Read header, check hasAppendedSchemaData flag              │
│  2. Apply standard type deltas                                 │
│  3. If flag set: process appended data section                 │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ DeltaSchemaAppendDataApplicator                                 │
│  Algorithm:                                                     │
│    totalBytes ← ReadVarLong()                                  │
│    if totalBytes = 0: return                                    │
│                                                                 │
│    for each type in appended data:                             │
│      typeState ← LookupTypeState(typeName)                     │
│      if typeState = null: skip type data                       │
│                                                                 │
│      for each field in type:                                   │
│        fieldIndex ← typeState.schema.getPosition(fieldName)    │
│        if fieldIndex = -1: skip field data                     │
│                                                                 │
│        for each (ordinal, value):                              │
│          shardIndex ← ordinal & shardMask                      │
│          dataElements ← shards[shardIndex].dataElements        │
│          WriteValue(dataElements, ordinal, fieldIndex, value)  │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│ HollowObjectTypeDataElements                                    │
│  - Bit-packed fixed-length storage (INT, LONG, etc.)          │
│  - Sequential variable-length storage (STRING, BYTES)          │
│  - Integrated with snapshot data                               │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Data Application Algorithm

```
ALGORITHM ApplyAppendedData(blobInput, readStateEngine)
  INPUT: blobInput : HollowBlobInput
         readStateEngine : HollowReadStateEngine
  OUTPUT: void (modifies readStateEngine state)

  totalByteLength ← ReadVarLong(blobInput)
  IF totalByteLength = 0:
    RETURN

  numTypes ← ReadVarInt(blobInput)

  FOR i ← 1 TO numTypes:
    typeName ← ReadString(blobInput)
    typeState ← readStateEngine.getTypeState(typeName)

    IF typeState = null OR typeState NOT instanceof HollowObjectTypeReadState:
      SkipTypeData(blobInput)
      CONTINUE

    schema ← typeState.schema
    numFields ← ReadVarInt(blobInput)

    FOR j ← 1 TO numFields:
      fieldName ← ReadString(blobInput)
      fieldType ← FieldType.values()[ReadByte(blobInput)]

      IF fieldType = REFERENCE:
        referencedType ← ReadString(blobInput)

      fieldIndex ← schema.getPosition(fieldName)

      IF fieldIndex = -1 OR schema.getFieldType(fieldIndex) ≠ fieldType:
        SkipFieldData(blobInput, fieldType)
        CONTINUE

      numOrdinals ← ReadVarInt(blobInput)

      FOR k ← 1 TO numOrdinals:
        ordinal ← ReadVarInt(blobInput)
        value ← ReadValue(blobInput, fieldType)

        // Calculate correct shard for this ordinal
        shardMask ← typeState.numShards - 1
        shardIndex ← ordinal AND shardMask
        dataElements ← typeState.shards[shardIndex].dataElements

        // Write directly to data elements
        WriteValueToDataElements(dataElements, ordinal, fieldIndex, value, fieldType)
```

### 4.3 Data Element Integration

Appended values are written directly into `HollowObjectTypeDataElements`, which maintains two storage regions:

**Fixed-Length Data (Bit-Packed):**
```
For field f at fieldIndex i, ordinal o:
  bitOffset = (o × bitsPerRecord) + bitOffsetPerField[i]

Field types and their bit allocations:
  INT, REFERENCE:  32 bits
  LONG:            64 bits
  BOOLEAN:         2 bits (0=false, 1=true, 3=null)
  FLOAT:           32 bits (IEEE 754 bit representation)
  DOUBLE:          64 bits (IEEE 754 bit representation)
```

**Variable-Length Data (Sequential):**
```
For STRING/BYTES fields:
  - Fixed-length region stores 40-bit byte range pointers: [startByte, endByte)
  - Variable-length region stores actual data sequentially

For ordinal o, field f:
  fixedData[bitOffset] = endByte(o, f)
  startByte(o, f) = endByte(o-1, f)  // or 0 if o=0
  actualData = varLengthData[f][startByte : endByte]
```

**Write Operations:**
```java
PROCEDURE WriteValueToDataElements(dataElements, ordinal, fieldIndex, value, fieldType)
  bitOffset ← (ordinal × dataElements.bitsPerRecord) + dataElements.bitOffsetPerField[fieldIndex]

  SWITCH fieldType:
    CASE INT, REFERENCE:
      dataElements.fixedLengthData.setElementValue(bitOffset, 32, value)

    CASE LONG:
      dataElements.fixedLengthData.setLargeElementValue(bitOffset, 64, value)

    CASE FLOAT:
      bits ← Float.floatToIntBits(value)
      dataElements.fixedLengthData.setElementValue(bitOffset, 32, bits)

    CASE DOUBLE:
      bits ← Double.doubleToLongBits(value)
      dataElements.fixedLengthData.setLargeElementValue(bitOffset, 64, bits)

    CASE BOOLEAN:
      dataElements.fixedLengthData.setElementValue(bitOffset, 2, value ? 1 : 0)

    CASE STRING:
      WriteVariableLengthString(dataElements, ordinal, fieldIndex, value)

    CASE BYTES:
      WriteVariableLengthBytes(dataElements, ordinal, fieldIndex, value)
```

### 4.4 Multi-Shard Support

Hollow distributes ordinals across shards using bit-masking:

```
Given N shards (N must be power of 2):
  shardMask = N - 1
  shardIndex(ordinal) = ordinal & shardMask

Example with 4 shards (mask = 0b11):
  ordinal  binary    shard
  0        ...00  →  0
  1        ...01  →  1
  2        ...10  →  2
  3        ...11  →  3
  4        ...00  →  0
  5        ...01  →  1
```

The applicator must write each ordinal to its corresponding shard:

```java
FOR EACH (ordinal, value):
  shardIndex ← ordinal AND shardMask
  targetShard ← shards[shardIndex]
  WriteToShard(targetShard, ordinal, fieldIndex, value)
```

**Critical Correctness Property:**
Failure to route writes to correct shards results in data corruption, as reads will query the correct shard but find no data (or data for a different ordinal).

---

## 5. Storage Architecture

### 5.1 Data Element Structure

```
HollowObjectTypeDataElements
├── FixedLengthData (bit-packed)
│   └── Stores INT, LONG, BOOLEAN, FLOAT, DOUBLE, REFERENCE
│       Layout: [record₀][record₁]...[recordₙ]
│       Each record: [field₀ bits][field₁ bits]...[fieldₘ bits]
│
└── VariableLengthData[] (per-field sequential)
    ├── Field 0: null (if not STRING/BYTES)
    ├── Field 1: [data₀₀₁₁][data₁₀₂₅]...[dataₙₓᵧ]  (if STRING/BYTES)
    └── Field m: ...

Where:
  - data₀₀₁₁ means: ordinal 0, bytes [0, 11)
  - Pointers stored in FixedLengthData
```

### 5.2 Bit-Packing Layout

For a schema with fields `{id: INT, name: STRING, value: LONG}`:

```
Fixed-Length Region (per ordinal):
┌────────┬──────────┬──────────┐
│ id     │ namePtr  │ value    │  72 bits per record
│ 32b    │ 40b      │ 64b      │  (136 bits actual, padded to long boundary)
└────────┴──────────┴──────────┘
 bits[0]  bits[32]   bits[72]   bits[136]

Variable-Length Region (for "name" field):
┌───────────┬───────────┬───────────┐
│ "Alice"   │ "Bob"     │ "Charlie" │
│ (5 bytes) │ (3 bytes) │ (7 bytes) │
└───────────┴───────────┴───────────┘
 byte[0]     byte[5]     byte[8]     byte[15]

Pointers in Fixed-Length:
  ordinal 0: namePtr = 5   (endByte for "Alice")
  ordinal 1: namePtr = 8   (endByte for "Bob")
  ordinal 2: namePtr = 15  (endByte for "Charlie")

  startByte(0) = 0
  startByte(1) = endByte(0) = 5
  startByte(2) = endByte(1) = 8
```

### 5.3 Write Semantics

**Initialization:**
```java
PROCEDURE PrepareForWrite(dataElements, maxOrdinal)
  // Calculate bit allocations
  bitsPerRecord ← 0
  FOR EACH field IN schema:
    bitOffsetPerField[field.index] ← bitsPerRecord
    bitsPerField[field.index] ← DetermineBitsForType(field.type)
    bitsPerRecord ← bitsPerRecord + bitsPerField[field.index]

  // Allocate fixed-length storage
  totalBits ← bitsPerRecord × (maxOrdinal + 1)
  fixedLengthData ← AllocateFixedLengthData(totalBits)

  // Allocate variable-length storage for STRING/BYTES fields
  FOR EACH field WHERE field.type IN {STRING, BYTES}:
    varLengthData[field.index] ← AllocateVariableLengthData()
```

**Write INT:**
```java
PROCEDURE WriteInt(ordinal, fieldIndex, value)
  bitOffset ← (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]
  unsignedValue ← value & 0xFFFFFFFF
  fixedLengthData.setElementValue(bitOffset, 32, unsignedValue)
```

**Write STRING:**
```java
PROCEDURE WriteString(ordinal, fieldIndex, stringValue)
  // Get start position from previous ordinal's end position
  IF ordinal = 0:
    startByte ← 0
  ELSE:
    prevBitOffset ← ((ordinal - 1) × bitsPerRecord) + bitOffsetPerField[fieldIndex]
    startByte ← fixedLengthData.getElementValue(prevBitOffset, 40)

  // Encode string as VarInt characters (Hollow format)
  currentByte ← startByte
  FOR EACH char IN stringValue:
    currentByte ← WriteVarInt(varLengthData[fieldIndex], currentByte, char)
  endByte ← currentByte

  // Store end position pointer
  bitOffset ← (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]
  fixedLengthData.setElementValue(bitOffset, 40, endByte)
```

### 5.4 Read Semantics

Reading appended values uses identical methods as snapshot data:

```java
FUNCTION ReadInt(ordinal, fieldIndex) → int
  bitOffset ← (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]
  RETURN fixedLengthData.getElementValue(bitOffset, 32)

FUNCTION ReadString(ordinal, fieldIndex) → String
  bitOffset ← (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]
  endByte ← fixedLengthData.getElementValue(bitOffset, 40)

  IF ordinal = 0:
    startByte ← 0
  ELSE:
    prevBitOffset ← ((ordinal - 1) × bitsPerRecord) + bitOffsetPerField[fieldIndex]
    startByte ← fixedLengthData.getElementValue(prevBitOffset, 40)

  length ← endByte - startByte
  RETURN DecodeVarIntString(varLengthData[fieldIndex], startByte, length)
```

**Key Property:**
There is no API distinction between values from snapshots and values from delta appends. All values are accessed via standard `readInt()`, `readLong()`, `readString()` methods.
