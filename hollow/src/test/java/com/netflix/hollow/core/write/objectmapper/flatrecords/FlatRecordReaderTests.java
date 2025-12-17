package com.netflix.hollow.core.write.objectmapper.flatrecords;

import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowInline;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FlatRecordReaderTests {
    private HollowObjectMapper mapper;
    private FlatRecordWriter flatRecordWriter;

    @Before
    public void setUp() {
        mapper = new HollowObjectMapper(new HollowWriteStateEngine());
        mapper.initializeTypeState(SimpleObject.class);
        mapper.initializeTypeState(WithList.class);
        mapper.initializeTypeState(AllFieldTypes.class);
        mapper.initializeTypeState(ComplexObject.class);
        HollowSchemaIdentifierMapper schemaIdMapper = new FakeHollowSchemaIdentifierMapper(mapper.getStateEngine());
        flatRecordWriter = new FlatRecordWriter(mapper.getStateEngine(), schemaIdMapper);
    }

    @Test
    public void readSimpleObjectFields() {
        FlatRecord record = flatten(new SimpleObject(42, "hello"));
        FlatRecordReader reader = new FlatRecordReader(record);

        Assert.assertTrue(reader.hasMore());

        HollowSchema schema = reader.readSchema();
        Assert.assertEquals("SimpleObject", schema.getName());

        Assert.assertEquals(42, reader.readInt());
        Assert.assertEquals("hello", reader.readString());

        Assert.assertFalse(reader.hasMore());
    }

    @Test
    public void readNullString() {
        FlatRecord record = flatten(new SimpleObject(100, null));
        FlatRecordReader reader = new FlatRecordReader(record);

        reader.readSchema();
        Assert.assertEquals(100, reader.readInt());
        Assert.assertNull(reader.readString());

        Assert.assertFalse(reader.hasMore());
    }

    @Test
    public void readCollectionSize() {
        FlatRecord record = flatten(new WithList(Arrays.asList("a", "b", "c")));
        FlatRecordReader reader = new FlatRecordReader(record);

        // Children are written first: 3 String objects, then List, then WithList
        // Skip 3 String objects
        for (int i = 0; i < 3; i++) {
            HollowSchema schema = reader.readSchema();
            Assert.assertEquals("String", schema.getName());
            reader.skipSchema(schema);
        }

        // Now at the List
        HollowSchema listSchema = reader.readSchema();
        Assert.assertEquals(HollowSchema.SchemaType.LIST, listSchema.getSchemaType());

        int size = reader.readCollectionSize();
        Assert.assertEquals(3, size);
    }

    @Test
    public void resetMovesPointerToStart() {
        FlatRecord record = flatten(new SimpleObject(42, "test"));
        FlatRecordReader reader = new FlatRecordReader(record);

        reader.readSchema();
        reader.readInt();

        reader.reset();

        HollowSchema schema = reader.readSchema();
        Assert.assertEquals("SimpleObject", schema.getName());
        Assert.assertEquals(42, reader.readInt());
    }

    @Test
    public void resetToMovesPointerToPosition() {
        FlatRecord record = flatten(new SimpleObject(42, "test"));
        FlatRecordReader reader = new FlatRecordReader(record);

        reader.readSchema();
        int positionAfterSchema = reader.pointer;

        reader.readInt();
        reader.readString();

        reader.resetTo(positionAfterSchema);
        Assert.assertEquals(42, reader.readInt());
    }

    @Test
    public void skipFieldSkipsInt() {
        FlatRecord record = flatten(new SimpleObject(42, "hello"));
        FlatRecordReader reader = new FlatRecordReader(record);

        reader.readSchema();
        reader.skipField(HollowObjectSchema.FieldType.INT);

        Assert.assertEquals("hello", reader.readString());
        Assert.assertFalse(reader.hasMore());
    }

    @Test
    public void skipFieldSkipsString() {
        FlatRecord record = flatten(new SimpleObject(42, "hello"));
        FlatRecordReader reader = new FlatRecordReader(record);

        reader.readSchema();
        reader.readInt();
        reader.skipField(HollowObjectSchema.FieldType.STRING);

        Assert.assertFalse(reader.hasMore());
    }

    @Test
    public void skipFieldSkipsNullValues() {
        FlatRecord record = flatten(new SimpleObject(42, null));
        FlatRecordReader reader = new FlatRecordReader(record);

        reader.readSchema();
        reader.skipField(HollowObjectSchema.FieldType.INT);
        reader.skipField(HollowObjectSchema.FieldType.STRING);

        Assert.assertFalse(reader.hasMore());
    }

    @Test
    public void skipSchemaSkipsEntireObject() {
        FlatRecord record = flatten(new SimpleObject(99, "skip me"));
        FlatRecordReader reader = new FlatRecordReader(record);

        HollowSchema schema = reader.readSchema();
        reader.skipSchema(schema);

        Assert.assertFalse(reader.hasMore());
    }

    @Test
    public void skipSchemaSkipsNestedStructure() {
        // Create object with all field types to exercise all sizing code paths
        AllFieldTypes child = new AllFieldTypes(
            42,                          // int
            123456789L,                  // long
            3.14f,                       // float
            2.71828,                     // double
            true,                        // boolean
            "test string",               // string
            new byte[]{1, 2, 3, 4, 5}    // bytes
        );
        ComplexObject obj = new ComplexObject(
            child,
            Arrays.asList("a", "b"),
            new HashSet<>(Arrays.asList("x", "y")),
            createMap("k1", "v1", "k2", "v2")
        );
        FlatRecord record = flatten(obj);
        FlatRecordReader reader = new FlatRecordReader(record);

        // Skip all child records until we reach ComplexObject
        while (reader.hasMore()) {
            HollowSchema schema = reader.readSchema();
            if ("ComplexObject".equals(schema.getName())) {
                // Found the parent - verify we can read its reference field
                int childOrdinal = reader.readOrdinal();
                Assert.assertTrue(childOrdinal >= 0);
                return;
            }
            reader.skipSchema(schema);
        }
        Assert.fail("ComplexObject not found");
    }

    private static <K, V> Map<K, V> createMap(K k1, V v1, K k2, V v2) {
        Map<K, V> map = new HashMap<>();
        map.put(k1, v1);
        map.put(k2, v2);
        return map;
    }

    private FlatRecord flatten(Object obj) {
        flatRecordWriter.reset();
        mapper.writeFlat(obj, flatRecordWriter);
        return flatRecordWriter.generateFlatRecord();
    }

    public static class SimpleObject {
        int intField;
        @HollowInline
        String stringField;

        public SimpleObject(int intField, String stringField) {
            this.intField = intField;
            this.stringField = stringField;
        }
    }

    public static class WithList {
        List<String> items;

        public WithList(List<String> items) {
            this.items = items;
        }
    }

    public static class AllFieldTypes {
        int intField;
        long longField;
        float floatField;
        double doubleField;
        boolean booleanField;
        @HollowInline
        String stringField;
        byte[] bytesField;

        public AllFieldTypes(int intField,
                             long longField,
                             float floatField,
                             double doubleField,
                             boolean booleanField,
                             String stringField,
                             byte[] bytesField) {
            this.intField = intField;
            this.longField = longField;
            this.floatField = floatField;
            this.doubleField = doubleField;
            this.booleanField = booleanField;
            this.stringField = stringField;
            this.bytesField = bytesField;
        }
    }

    public static class ComplexObject {
        AllFieldTypes child;      // REFERENCE - exercises object with all field types
        List<String> listField;   // exercises LIST sizing
        Set<String> setField;     // exercises SET sizing
        Map<String, String> mapField; // exercises MAP sizing

        public ComplexObject(AllFieldTypes child,
                             List<String> listField,
                             Set<String> setField,
                             Map<String, String> mapField) {
            this.child = child;
            this.listField = listField;
            this.setField = setField;
            this.mapField = mapField;
        }
    }
}
