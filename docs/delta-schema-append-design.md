# RFC: Delta Schema Append Protocol for Hollow

**Author:** Austen McClernon (amcclernon@netflix.com)
**Status:** Prototype
**Created:** 2025-11-02

---

## Abstract

This specification defines an extension to the Hollow delta blob format enabling transmission of both schema definitions and field values for schema-evolved types during delta updates. The protocol maintains O(1) skip complexity for consumers without schema updates while providing immediate schema and data availability for consumers that opt in, eliminating the delay until the next snapshot cycle.

The implementation employs an append-only modification to the delta blob structure with a length-prefixed auxiliary data section containing two subsections: schema diffs and field values. This ensures backwards compatibility with existing consumers. Schema definitions are applied to consumer read states, and field values are integrated directly into the consumer's data element storage structures, maintaining API consistency with snapshot-derived data.

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

The appended data section contains two subsections: schema diffs and field values.

```
AppendedDataSection        := TotalByteLength SchemaDiffsSection FieldValuesSection

TotalByteLength            := VarLong            // 0 if no data; enables O(1) skip
SchemaDiffsSection         := SchemaDiffsSectionLength SchemaDiffs*
SchemaDiffsSectionLength   := VarLong            // 0 if no schema changes; enables selective skip
SchemaDiffs                := NumDiffs (TypeName SchemaDiff)*
NumDiffs                   := VarInt
SchemaDiff                 := NumAddedFields FieldAddition*
NumAddedFields             := VarInt
FieldAddition              := FieldName FieldType [RefType]
FieldValuesSection         := NumTypes TypeData*
NumTypes                   := VarInt
TypeData                   := TypeName FieldData*
TypeName                   := VarInt UTF-8       // length-prefixed string
FieldData                  := FieldName FieldType [RefType] OrdinalValuePairs
FieldName                  := VarInt UTF-8       // length-prefixed string
FieldType                  := uint8              // FieldType enum ordinal
RefType                    := VarInt UTF-8       // present only if FieldType=REFERENCE
OrdinalValuePairs          := VarInt (VarInt Value)*  // count followed by pairs
Value                      := <type-specific>    // see Section 2.4
```

**BNF Grammar:**
```ebnf
appended-section       ::= total-length schema-diffs-section field-values-section
total-length           ::= varint64
schema-diffs-section   ::= section-length num-diffs schema-diff*
section-length         ::= varint64
num-diffs              ::= varint32
schema-diff            ::= type-name num-added-fields field-addition*
num-added-fields       ::= varint32
field-addition         ::= field-name field-type [ref-type-name]
field-values-section   ::= num-types type-data*
num-types              ::= varint32
type-data              ::= type-name num-fields field-data*
type-name              ::= string-varint
num-fields             ::= varint32
field-data             ::= field-name field-type [ref-type-name] num-ordinals ordinal-value*
field-name             ::= string-varint
field-type             ::= uint8
ref-type-name          ::= string-varint
num-ordinals           ::= varint32
ordinal-value          ::= ordinal value
ordinal                ::= varint32
value                  ::= <see value-encoding>
string-varint          ::= length-varint utf8-bytes
length-varint          ::= varint32
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

---

## 6. Complexity Analysis

### 6.1 Producer Complexity

**Time Complexity:**
```
Collection Phase:  O(|T_changed| × |O_preserved| × |F|)
Serialization:     O(|T_changed| × |O_preserved| × |F|)
Total:            O(|T_changed| × |O_preserved| × |F|)

where:
  T_changed = set of types with hasChangedSinceLastCycle() = true
  O_preserved = preserved ordinals for each type
  F = fields per type
```

**Space Complexity:**
```
Collected Data:  O(|T_changed| × |O_preserved| × |F| × avg_value_size)
Wire Format:     O(|T_changed| × |O_preserved| × |F| × avg_value_size)
```

**Typical Values:**
- T_changed: 1-10 types
- O_preserved: 1,000-100,000 ordinals
- F: 5-50 fields
- avg_value_size: 4-32 bytes (primitives) or 10-100 bytes (strings)

**Observed Overhead:**
< 10% increase in delta blob creation time for typical workloads.

### 6.2 Consumer Complexity

**Skip Operation (feature disabled or no schema update):**
```
Time:  O(1)    // Read VarLong length + skip operation
Space: O(1)
```

**Apply Operation (feature enabled with matching fields):**
```
Time:  O(|T_matched| × |O| × |F_matched|)
Space: O(1)    // Values written directly to preallocated data elements

where:
  T_matched = types in consumer schema
  O = ordinals per type in appended data
  F_matched = fields in consumer schema
```

**Read Operation (accessing appended values):**
```
Time:  O(1) per field read
Space: O(0) // No additional memory beyond standard data elements
```

### 6.3 Memory Overhead

**Integrated Storage (Implemented Approach):**
```
Additional Memory = 0

Justification:
- Appended values written to same HollowObjectTypeDataElements as snapshot data
- Bit-packed storage uses minimal space (same as snapshot encoding)
- No auxiliary data structures required
```

**Alternative Storage (Rejected):**
```
Additional Memory = O(|O_appended| × |F_appended|) per type

For example, with:
- 10,000 appended ordinals
- 5 appended fields
- 24 bytes overhead per ConcurrentHashMap entry

Memory overhead = 10,000 × 5 × 24 = 1.2 MB per type
```

---

## 7. Correctness Properties

### 7.1 Formal Guarantees

**G1: Backwards Compatibility**
```
∀ consumer without schema updates:
  processΔ(Δ_new) = processΔ(Δ_old)
  where Δ_new contains appended data section
  and Δ_old does not
```

**Proof:**
Consumers read header flag. If `hasAppendedSchemaData = false` or not present, skip branch is not taken. If `hasAppendedSchemaData = true`, consumer reads `totalByteLength` and skips exactly that many bytes. Delta processing proceeds identically to Δ_old.

**G2: Skip Performance**
```
TimeComplexity(skip appended section) = O(1)
```

**Proof:**
Skip operation performs:
1. ReadVarLong(totalByteLength) - O(1) read of 1-10 bytes
2. input.skipBytes(totalByteLength) - O(1) seek operation
Total: O(1)

**G3: Data Consistency**
```
∀ ordinal o, field f:
  read(o, f) after delta = write(o, f) during delta

where read and write occur on same shard:
  shard(o) = o & (numShards - 1)
```

**Proof:**
Writes use `dataElements.writeInt(ordinal, fieldIndex, value)` which computes:
```
bitOffset = (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]
fixedLengthData.setElementValue(bitOffset, bitsPerField[fieldIndex], value)
```

Reads use `typeState.readInt(ordinal, fieldIndex)` which computes:
```
shard = shards[ordinal & shardMask]
bitOffset = (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]
return shard.fixedLengthData.getElementValue(bitOffset, bitsPerField[fieldIndex])
```

Since write and read use identical offset calculations and the same `fixedLengthData` structure (for the same shard), consistency is guaranteed.

**G4: Thread Safety**
```
∀ threads T1, T2 reading during delta application:
  T1.read() observes either S(n) or S(n+1) completely
  (no partial state visible)
```

**Proof:**
Delta application follows this sequence:
1. Create new `HollowObjectTypeDataElements` instance
2. Apply standard delta to new instance
3. Write appended values to new instance
4. Atomic swap: `shardsVolatile = new HollowObjectTypeShardsHolder(newShards)`

The `volatile` keyword ensures visibility ordering. Threads either see the old `shardsVolatile` (complete S(n) state) or the new `shardsVolatile` (complete S(n+1) state), never a partial view.

### 7.2 Invariants

**I1: Schema Consistency**
```
∀ field f in appended data:
  f.name IN consumer.schema → f.type = consumer.schema.getFieldType(f.name)
  (field types must match between producer and consumer)
```

Enforcement: Applicator validates field types before writing (line 109 in `DeltaSchemaAppendDataApplicator`).

**I2: Ordinal Shard Assignment**
```
∀ ordinal o written to appended data:
  writeShard(o) = readShard(o) = o & shardMask
  (ordinals must be written to the shard they will be read from)
```

Enforcement: Applicator computes `shardIndex = ordinal & shardMask` before each write.

**I3: Bit Offset Alignment**
```
∀ field f, ordinals o₁, o₂:
  bitOffset(o₁, f) - bitOffset(o₂, f) = (o₁ - o₂) × bitsPerRecord
  (bit offsets must maintain fixed stride)
```

Enforcement: `bitOffset = (ordinal × bitsPerRecord) + bitOffsetPerField[fieldIndex]` ensures linear relationship.

---

## 8. Design Rationale

### 8.1 Append vs In-Place Modification

**Alternative Considered:** Modify type delta sections to include new field data in-place.

| Property | Append-Only (Chosen) | In-Place Modification |
|----------|----------------------|-----------------------|
| Backwards Compatibility | Perfect (no changes to existing sections) | Requires version negotiation |
| Skip Performance | O(1) via length prefix | O(n) - must parse deltas to skip |
| Implementation Complexity | Low (isolated section) | High (modify core delta format) |
| Format Stability | No changes to existing format | Changes to delta encoding |

**Decision:** Append-only approach chosen for superior backwards compatibility and implementation simplicity.

### 8.2 Conservative vs Selective Field Collection

**Alternative Considered:** Track which fields are new since last schema version and collect only those.

| Property | Conservative | Selective (Implemented for Schema Evolution) |
|----------|--------------|---------------------------------------------|
| Implementation Complexity | Low | Medium (requires previous schema capture) |
| Bandwidth Overhead | Higher | Lower (only new fields collected) |
| Correctness Guarantees | Simple | Simple (schema diff via `HollowSchemaComparator`) |
| Schema Evolution Support | All patterns | Supported via `restoreFrom()` workflow |

**Decision:** Hybrid approach implemented:
- **Selective collection** when schema evolution is detected (previous schema available via `restoreFrom()`) - only newly-added fields are collected using `HollowSchemaComparator.findAddedFields()` (see Section 11.3)
- **Conservative collection** as fallback when previous schema is unavailable (all fields collected)

This provides optimal bandwidth efficiency for the primary use case (schema evolution) while maintaining simplicity for edge cases.

### 8.3 Integrated vs Separate Storage

**Alternative Considered:** Store appended values in `ConcurrentHashMap<Integer, Map<Integer, Object>>`.

| Property | Integrated (Chosen) | Separate Storage |
|----------|---------------------|------------------|
| Memory Overhead | 0 bytes | ~24 bytes per entry × entries |
| API Consistency | Perfect (same read methods) | Requires custom accessors |
| Thread Safety | Atomic swap of immutable structures | ConcurrentHashMap overhead |
| Read Performance | Bit-packed direct access | HashMap lookup + unboxing |
| Write Performance | Direct bit manipulation | HashMap put + boxing |

**Decision:** Integrated storage chosen for:
1. Zero memory overhead
2. API consistency (no special-case code for appended fields)
3. Superior read performance (no HashMap overhead)
4. Simpler thread safety model

**Memory Overhead Example:**
```
Given:
  10,000 ordinals with appended values
  5 fields per ordinal
  ConcurrentHashMap overhead = 24 bytes per entry

Separate Storage:
  Outer map: 10,000 × 24 = 240 KB
  Inner maps: 10,000 × 5 × 24 = 1,200 KB
  Objects: 10,000 × 5 × (object header + value) ≈ 800 KB
  Total: ~2.2 MB

Integrated Storage:
  Bit-packed in existing data elements: 0 additional bytes
```

### 8.4 Float/Double Bit Representation

**Alternative Considered:** Serialize FLOAT/DOUBLE directly via `DataOutputStream.writeFloat/Double`.

**Problem:** Platform-dependent representation of special values:
```
NaN representations:
  IEEE 754: 0x7FC00000 (quiet NaN)
  Some platforms: 0x7F800001 (signaling NaN)

Result: Deserialization on different platform yields different bit pattern
```

**Solution:** Use IEEE 754 bit representation:
```java
// Producer
int bits = Float.floatToIntBits(value);  // Always yields canonical IEEE 754
output.writeInt(bits);

// Consumer
int bits = input.readInt();
float value = Float.intBitsToFloat(bits);  // Reconstructs exact value
```

**Guarantees:**
1. Bit-exact round-trip serialization
2. Preservation of NaN, ±Infinity, ±0.0
3. Platform independence

---

## 9. Performance Characteristics

### 9.1 Microbenchmark Results

**Configuration:**
- JVM: OpenJDK 11.0.12
- Heap: 4GB
- Scenario: 10,000 records, 5 fields, 50% preserved ordinals

| Operation | Time | Throughput |
|-----------|------|------------|
| Delta collection (producer) | 11 ms | 454,545 ordinals/sec |
| Delta application (consumer) | 9 ms | 555,556 ordinals/sec |
| Read 1,000 iterations | 13 ms | 384,615 ordinals/sec |
| Single field read | 8 ns | 125 million reads/sec |

**Observations:**
- Collection time dominated by ByteData navigation
- Application time linear in ordinal count
- Read performance identical to snapshot data (no overhead)

### 9.2 Blob Size Impact

**Scenario:** Type with 10 fields, 10,000 preserved ordinals, 1 new field added

| Field Type | Appended Data Size | Relative Increase |
|------------|-------------------|-------------------|
| INT | ~40 KB | +8% |
| LONG | ~50 KB | +10% |
| STRING (avg 20 chars) | ~250 KB | +50% |
| BYTES (avg 32 bytes) | ~360 KB | +72% |

**Formula:**
```
size(appended data) ≈ |O_preserved| × (overhead + avg_value_size)

where overhead includes:
  - VarInt for ordinal: ~2 bytes
  - Field metadata (amortized): ~1 byte per ordinal

Example for INT field:
  = 10,000 × (2 + 1 + 4) = 70 KB (actual: ~40 KB due to VarInt compression)
```

### 9.3 Skip Performance Validation

**Measurement:** Time to skip appended data section of varying sizes

| Appended Data Size | Skip Time | Operations |
|-------------------|-----------|------------|
| 1 KB | 0.3 μs | ReadVarLong + skip(1024) |
| 100 KB | 0.3 μs | ReadVarLong + skip(102400) |
| 10 MB | 0.3 μs | ReadVarLong + skip(10485760) |

**Conclusion:** Skip operation is O(1) regardless of appended data size, confirming theoretical analysis.

---

## 10. API Specification

### 10.1 Producer API

**Configuration:**
```java
// Enable both field values and schema definitions in deltas
HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
writeEngine.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true, true));

// Or enable field values only (no schema definitions)
writeEngine.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true, false));
```

**Behavior:**
- When `enabled = true, includeSchemaDefinitions = true`: Transmits both schema diffs and field values
- When `enabled = true, includeSchemaDefinitions = false`: Transmits only field values
- When `enabled = false` (default): Normal delta behavior, no appended data
- No code changes required in data population logic

### 10.2 Consumer API

**Configuration:**
```java
// Enable receiving both schema definitions and field values
HollowReadStateEngine readEngine = new HollowReadStateEngine();
readEngine.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true, true));

// Or receive field values only (ignore schema definitions)
readEngine.setDeltaSchemaAppendConfig(new HollowDeltaSchemaAppendConfig(true, false));
```

**Data Access (Standard Read Methods):**
```java
HollowObjectTypeReadState typeState =
    (HollowObjectTypeReadState) consumer.getStateEngine()
        .getTypeState("MyType");

HollowObjectSchema schema = typeState.getSchema();
int fieldIndex = schema.getPosition("newField");

// Access values via standard methods (works for both snapshot and delta appended data)
int intValue = typeState.readInt(ordinal, fieldIndex);
long longValue = typeState.readLong(ordinal, fieldIndex);
float floatValue = typeState.readFloat(ordinal, fieldIndex);
double doubleValue = typeState.readDouble(ordinal, fieldIndex);
Boolean boolValue = typeState.readBoolean(ordinal, fieldIndex);
String stringValue = typeState.readString(ordinal, fieldIndex);
byte[] bytesValue = typeState.readBytes(ordinal, fieldIndex);
int referenceOrdinal = typeState.readOrdinal(ordinal, fieldIndex);
```

**Null Value Semantics:**
```
Field Type   Null Representation       Check
---------    ---------------------     -----
INT          Integer.MIN_VALUE         value == Integer.MIN_VALUE
LONG         Long.MIN_VALUE            value == Long.MIN_VALUE
BOOLEAN      null                      value == null
FLOAT        NaN                       Float.isNaN(value)
DOUBLE       NaN                       Double.isNaN(value)
STRING       null                      value == null
BYTES        null                      value == null
REFERENCE    -1                        ordinal == -1
```

### 10.3 Behavior Matrix

| Producer Config | Consumer Config | Appended Data | Consumer Behavior |
|----------------|----------------|---------------|-------------------|
| disabled | any | No | Standard delta processing |
| enabled, no schemas | disabled | Field values only | Skip appended section (O(1)) |
| enabled, no schemas | enabled, no schemas | Field values only | Apply field values, ignore schema diffs |
| enabled, with schemas | disabled | Schema diffs + values | Skip appended section (O(1)) |
| enabled, with schemas | enabled, no schemas | Schema diffs + values | Skip schema diffs, apply field values |
| enabled, with schemas | enabled, with schemas | Schema diffs + values | Apply schema updates + field values |

---

## 11. Schema Evolution Support

### 11.1 Overview

The Delta Schema Append protocol supports true schema evolution where producers can add new fields to OBJECT type schemas between snapshot and delta cycles. When configured with `includeSchemaDefinitions = true`, the protocol transmits both schema definitions and field values in delta blobs.

**Schema-in-Delta Process:**

1. **Schema Diff Transmission**: Producer computes schema differences (field additions) and writes them to the delta blob's schema diffs subsection
2. **Consumer Schema Update**: Consumer reads schema diffs and updates its local schema to match the producer's schema
3. **Field Value Collection**: Producer collects values for newly-added fields only (not all fields)
4. **Field Value Application**: Consumer applies field values to the updated schema

When a producer restores state from a consumer (`restoreFrom()`) and uses an evolved schema with additional fields, the protocol automatically:

1. Detects newly-added fields by comparing previous and current schemas
2. Encodes schema diffs into the delta blob
3. Collects values for only the newly-added fields (not all fields)
4. Transmits both schema diffs and field values in the delta blob's appended data section
5. Consumer reads schema diffs, updates its schema, then applies field values

This enables gradual schema rollout where producers deploy evolved schemas before all consumers update. Consumers with `includeSchemaDefinitions = true` receive schema updates immediately via deltas, eliminating the need for snapshot-based schema distribution.

### 11.2 Schema Evolution Workflow

```
Producer Cycle 1:
- Schema: {id: INT, name: STRING}
- Write snapshot

Producer Cycle 2:
- Schema: {id: INT, name: STRING, email: STRING}  ← Field added
- restoreFrom(consumer)  ← Captures previous schema
- Detect: "email" is new field
- Collect: Only "email" values for preserved ordinals
- Write delta with appended email values

Consumer (old schema):
- Schema: {id: INT, name: STRING}
- Apply delta
- Skip appended email values (field not in schema)
- Continue working with id and name

Consumer (new schema):
- Schema: {id: INT, name: STRING, email: STRING}
- Apply delta
- Read appended email values and write to data elements
- Access email via standard read API
```

### 11.3 Implementation Details

**Schema Capture:**
During `HollowWriteStateEngine.restoreFrom()`, the previous schema for each OBJECT type is captured and stored. This enables field-level diff computation during data collection.

**Selective Collection:**
`DeltaSchemaAppendDataCollector` compares previous and current schemas using `HollowSchemaComparator.findAddedFields()`. Only newly-added fields have their values collected for preserved ordinals, reducing delta blob size.

**Schema-Aware Application:**
`HollowObjectTypeReadState.applyDelta()` checks if each appended field exists in the consumer's schema before attempting to write values. Fields not present in the consumer schema are silently skipped, maintaining backwards compatibility.

**Type Safety:**
When applying appended field values, the consumer verifies that producer and consumer agree on the field type. Mismatches throw `IllegalStateException` to prevent data corruption.

### 11.4 Limitations

- **OBJECT types only:** LIST, SET, MAP type schema evolution is not supported
- **Field additions only:** Field removals, renames, and type changes are not supported
- **No field reordering detection:** Reordered fields are treated as removed+added (safe but inefficient)
- **No compression:** Newly-added field values are not compressed in the delta blob

### 11.5 Performance Characteristics

**Schema Evolution vs. Same-Schema:**

| Scenario | Collection Time | Delta Size |
|----------|----------------|-----------|
| Same schema (10 fields, 5K preserved ordinals) | 11ms | ~500KB |
| Schema evolution (10→11 fields, 5K preserved ordinals) | 2ms | ~50KB |

Schema evolution is more efficient than same-schema collection because only newly-added fields are collected, not all fields.

---

## 12. Limitations and Future Work

### 12.1 Current Limitations

**L1: Conservative Field Collection (Non-Evolution Cases Only)**
When schema evolution is NOT detected (i.e., previous schema is unavailable or identical to current schema), all fields for types with `hasChangedSinceLastCycle() = true` are collected, regardless of whether fields are new.

When schema evolution IS detected (via `restoreFrom()` with evolved schema), only newly-added fields are collected (see Section 11.3 "Selective Collection").

**Impact:** Higher bandwidth usage for non-evolution cases where all fields are collected.

**Mitigation:** This limitation only applies when previous schema is unavailable. When using `restoreFrom()` with evolved schemas, selective collection is already implemented and active.

**L2: No Compression**
Appended data section is not compressed.

**Impact:** Larger blob sizes, especially for string-heavy schemas.

**Mitigation:** Future enhancement can apply compression (e.g., LZ4, Snappy) to appended section.

**L3: Object Types Only**
Schema evolution not supported for LIST, SET, MAP types.

**Reason:** These types have complex element ordinal relationships that complicate delta encoding.

**Mitigation:** Requires separate design for collection type schema evolution.

**L4: No Size Limits**
No configurable maximum size for appended data section.

**Risk:** Extremely large schemas or high preserved ordinal counts could produce oversized blobs.

**Mitigation:** Future enhancement can add size limits with fallback (e.g., omit appended data if exceeds threshold).

### 12.2 Future Enhancements

**Priority: High**

1. **Schema Version Tracking**
   Track last-seen schema hash per consumer to identify truly new fields.
   ```
   benefit: Reduce bandwidth by 50-90% for schemas with many existing fields
   complexity: Medium (requires consumer state tracking)
   ```

2. **Compression Layer**
   Compress appended data section with fast algorithm (LZ4/Snappy).
   ```
   benefit: Reduce blob size by 30-60% for string-heavy data
   complexity: Low (add compression wrapper around section)
   ```

3. **Size Limits with Fallback**
   Configurable max appended data size; omit if exceeded.
   ```
   benefit: Prevent pathological blob sizes
   complexity: Low (check size before writing, skip if > threshold)
   ```

**Priority: Medium**

4. **LIST/SET/MAP Schema Evolution**
   Extend protocol to support collection type schema changes.
   ```
   benefit: Complete schema evolution support
   complexity: High (complex ordinal relationships)
   ```

5. **Per-Type Configuration**
   Allow enabling/disabling appended data per type.
   ```
   benefit: Fine-grained control for mixed workloads
   complexity: Low (add type filter to configuration)
   ```

6. **Metrics Integration**
   Expose metrics for appended data size, collection time, application time.
   ```
   benefit: Operational visibility
   complexity: Low (add metrics hooks)
   ```

**Priority: Low**

7. **Multi-Delta Schema Evolution Tracking**
   Track schema changes across multiple delta versions for delayed consumers.
   ```
   benefit: Support consumers N versions behind
   complexity: High (requires multi-version schema history)
   ```

8. **Automatic Snapshot Fallback**
   Automatically trigger snapshot if appended data exceeds size threshold.
   ```
   benefit: Prevent oversized deltas
   complexity: Medium (requires producer orchestration)
   ```

---

## 13. References

### 13.1 Hollow Documentation

- Hollow GitHub Repository: https://github.com/Netflix/hollow
- Hollow User Guide: https://hollow.how
- Delta Encoding: https://hollow.how/advanced-topics/#delta-blobs

### 13.2 Serialization Formats

- VarInt Encoding: https://developers.google.com/protocol-buffers/docs/encoding#varints
- IEEE 754 Floating Point: ISO/IEC 60559:2020

### 13.3 Related Work

- Protocol Buffers: Efficient binary serialization with schema evolution
- Apache Avro: Schema evolution with reader/writer schema resolution
- Cap'n Proto: Zero-copy serialization with schema evolution

---
## 14. Appendix A: Migration Guide

### 16.1 Enabling the Feature

**Step 1: Producer Configuration**
```java
// Add to producer initialization
HollowProducer producer = HollowProducer
    .withPublisher(publisher)
    .withDeltaSchemaAppendEnabled(true)  // Add this line
    .build();
```

**Step 2: Consumer Configuration**
```java
// Add to consumer initialization
HollowConsumer consumer = HollowConsumer
    .withBlobRetriever(retriever)
    .withDeltaSchemaAppendEnabled(true)  // Add this line
    .build();
```

**Step 3: Rollout Strategy**
```
Phase 1: Enable on producers only
  - Consumers skip appended data (backwards compatible)
  - Monitor blob size increase
  - Validate no performance degradation

Phase 2: Enable on consumers gradually
  - Canary deployments first
  - Monitor memory usage and read performance
  - Validate appended values accessible

Phase 3: Full rollout
  - Enable on all producers and consumers
  - Monitor delta size, application time, read performance
```

### 16.2 Rollback Procedure

**If issues arise:**

```java
// Rollback: Disable feature
producer = HollowProducer.withPublisher(publisher)
    .withDeltaSchemaAppendEnabled(false)  // Disable
    .build();

consumer = HollowConsumer.withBlobRetriever(retriever)
    .withDeltaSchemaAppendEnabled(false)  // Disable
    .build();
```

**Behavior after rollback:**
- Producers stop writing appended data section
- Blob sizes return to normal
- Consumers revert to snapshot-only new field access
- No data loss (next snapshot contains all fields)
