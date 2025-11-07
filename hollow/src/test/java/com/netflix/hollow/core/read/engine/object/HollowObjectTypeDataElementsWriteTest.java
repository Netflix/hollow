package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import org.junit.Assert;
import org.junit.Test;

public class HollowObjectTypeDataElementsWriteTest {

    @Test
    public void testWriteIntField() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("value", FieldType.INT);

        HollowObjectTypeDataElements dataElements = new HollowObjectTypeDataElements(schema, WastefulRecycler.DEFAULT_INSTANCE);

        // Initialize data elements for 3 ordinals
        dataElements.maxOrdinal = 2;
        dataElements.prepareForWrite();

        // Write values to ordinal 1
        dataElements.writeInt(1, 0, 100);  // field 0 = 100
        dataElements.writeInt(1, 1, 200);  // field 1 = 200

        // Create read shard and verify
        HollowObjectTypeReadStateShard shard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);

        Assert.assertEquals(100, shard.readInt(1, 0));
        Assert.assertEquals(200, shard.readInt(1, 1));
    }

    @Test
    public void testWriteLongField() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("value", FieldType.LONG);

        HollowObjectTypeDataElements dataElements = new HollowObjectTypeDataElements(schema, WastefulRecycler.DEFAULT_INSTANCE);
        dataElements.maxOrdinal = 1;
        dataElements.prepareForWrite();

        dataElements.writeLong(0, 0, 9876543210L);

        HollowObjectTypeReadStateShard shard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);
        Assert.assertEquals(9876543210L, shard.readLong(0, 0));
    }

    @Test
    public void testWriteLongFieldTwiceDoesNotCorrupt() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("value", FieldType.LONG);

        HollowObjectTypeDataElements dataElements = new HollowObjectTypeDataElements(schema, WastefulRecycler.DEFAULT_INSTANCE);
        dataElements.maxOrdinal = 1;
        dataElements.prepareForWrite();

        // Write first value
        dataElements.writeLong(0, 0, 1234567890L);

        // Write second value (should replace, not OR with first)
        dataElements.writeLong(0, 0, 9876543210L);

        HollowObjectTypeReadStateShard shard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);
        Assert.assertEquals(9876543210L, shard.readLong(0, 0));
    }

    @Test
    public void testWriteStringField() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("name", FieldType.STRING);

        HollowObjectTypeDataElements dataElements = new HollowObjectTypeDataElements(schema, WastefulRecycler.DEFAULT_INSTANCE);
        dataElements.maxOrdinal = 1;
        dataElements.prepareForWrite();

        dataElements.writeString(0, 0, "test string");

        HollowObjectTypeReadStateShard shard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);

        // Read string using shard's API (needs start/end bytes)
        int fieldIndex = 0;
        int numBitsForField = dataElements.bitsPerField[fieldIndex];
        long endByte = dataElements.fixedLengthData.getElementValue(0, numBitsForField);
        long startByte = 0;

        Assert.assertEquals("test string", shard.readString(startByte, endByte, numBitsForField, fieldIndex));
    }

    @Test
    public void testWriteReferenceField() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 1);
        schema.addField("ref", FieldType.REFERENCE, "ReferencedType");

        HollowObjectTypeDataElements dataElements = new HollowObjectTypeDataElements(schema, WastefulRecycler.DEFAULT_INSTANCE);
        dataElements.maxOrdinal = 1;
        dataElements.prepareForWrite();

        dataElements.writeReference(0, 0, 42);

        HollowObjectTypeReadStateShard shard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);
        Assert.assertEquals(42, shard.readOrdinal(0, 0));
    }

    @Test
    public void testWriteNullField() {
        HollowObjectSchema schema = new HollowObjectSchema("TestType", 2);
        schema.addField("value", FieldType.INT);
        schema.addField("name", FieldType.STRING);

        HollowObjectTypeDataElements dataElements = new HollowObjectTypeDataElements(schema, WastefulRecycler.DEFAULT_INSTANCE);
        dataElements.maxOrdinal = 1;
        dataElements.prepareForWrite();

        dataElements.writeNull(0, 0);  // INT field
        dataElements.writeNull(0, 1);  // STRING field

        HollowObjectTypeReadStateShard shard = new HollowObjectTypeReadStateShard(schema, dataElements, 0);
        // NULL_INT_RETURN_VALUE is implementation-specific, check data elements shows null
        Assert.assertTrue(isNullValue(dataElements, 0, 0));
    }

    private boolean isNullValue(HollowObjectTypeDataElements dataElements, int ordinal, int fieldIndex) {
        long bitOffset = ((long) ordinal * dataElements.bitsPerRecord) + dataElements.bitOffsetPerField[fieldIndex];
        long value = dataElements.bitsPerField[fieldIndex] <= 56 ?
                dataElements.fixedLengthData.getElementValue(bitOffset, dataElements.bitsPerField[fieldIndex])
                : dataElements.fixedLengthData.getLargeElementValue(bitOffset, dataElements.bitsPerField[fieldIndex]);
        return value == dataElements.nullValueForField[fieldIndex];
    }
}
