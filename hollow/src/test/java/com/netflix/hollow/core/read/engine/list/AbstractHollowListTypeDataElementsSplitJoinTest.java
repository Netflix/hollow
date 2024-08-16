package com.netflix.hollow.core.read.engine.list;

import static org.mockito.Mockito.when;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowListTypeDataElementsSplitJoinTest extends AbstractStateEngineTest {
    protected HollowObjectSchema objectSchema;
    protected HollowListSchema listSchema;

    @Mock
    protected HollowListTypeReadState mockListTypeState;

    @Before
    public void setUp() {
        this.objectSchema = new HollowObjectSchema("TestObject", 4);
        this.objectSchema.addField("longField", HollowObjectSchema.FieldType.LONG);
        this.objectSchema.addField("stringField", HollowObjectSchema.FieldType.STRING);
        this.objectSchema.addField("intField", HollowObjectSchema.FieldType.INT);
        this.objectSchema.addField("doubleField", HollowObjectSchema.FieldType.DOUBLE);

        this.listSchema = new HollowListSchema("TestList", "TestObject");

        MockitoAnnotations.initMocks(this);
        HollowListTypeDataElements[] fakeDataElements = new HollowListTypeDataElements[5];
        when(mockListTypeState.currentDataElements()).thenReturn(fakeDataElements);
        super.setUp();
    }

    @Override
    protected void initializeTypeStates() {
        writeStateEngine.setTargetMaxTypeShardSize(4096);
        writeStateEngine.addTypeState(new HollowObjectTypeWriteState(objectSchema));
        writeStateEngine.addTypeState(new HollowListTypeWriteState(listSchema));
    }

    private void populateWriteStateEngine(int numRecords, int[][] listContents) {
        initWriteStateEngine();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(objectSchema);
        for(int i=0;i<numRecords;i++) {
            rec.reset();
            rec.setLong("longField", i);
            rec.setString("stringField", "Value" + i);
            rec.setInt("intField", i);
            rec.setDouble("doubleField", i);

            writeStateEngine.add("TestObject", rec);
        }
        for(int[] list : listContents) {
            addRecord(Arrays.stream(list).toArray());
        }
    }

    private void populateWriteStateEngine(int[] recordIds, int[][] listContents) {
        initWriteStateEngine();
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(objectSchema);
        for(int recordId : recordIds) {
            rec.reset();
            rec.setLong("longField", recordId);
            rec.setString("stringField", "Value" + recordId);
            rec.setInt("intField", recordId);
            rec.setDouble("doubleField", recordId);

            writeStateEngine.add("TestObject", rec);
        }
        for(int[] list : listContents) {
            addRecord(Arrays.stream(list).toArray());
        }
    }

    private void addRecord(int... ordinals) {
        HollowListWriteRecord rec = new HollowListWriteRecord();

        for(int i=0;i<ordinals.length;i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestList", rec);
    }


    protected HollowListTypeReadState populateTypeStateWith(int numRecords, int[][] listContents) throws IOException {
        populateWriteStateEngine(numRecords, listContents);
        roundTripSnapshot();
        return (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
    }

    protected HollowListTypeReadState populateTypeStateWith(int[] recordIds, int[][] listContents) throws IOException {
        populateWriteStateEngine(recordIds, listContents);
        roundTripSnapshot();
        return (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
    }

    protected void assertDataUnchanged(int[][] listContents) {
        assertDataUnchanged((HollowListTypeReadState) readStateEngine.getTypeState("TestList"), listContents);
    }

    protected void assertDataUnchanged(HollowListTypeReadState typeState, int[][] listContents) {
        int numListRecords = listContents.length;
        for(int i=0;i<numListRecords;i++) {
            HollowOrdinalIterator iter = typeState.ordinalIterator(i);
            for(int j=0;j<listContents[i].length;j++) {
                Assert.assertEquals(listContents[i][j], iter.next());
            }
            Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
            // System.out.println(obj.toString());
            // assertEquals(i, obj.get("longField"));
            // assertEquals("Value"+i, obj.getString("stringField"));
            // assertEquals((double)i, obj.getDouble("doubleField"), 0);
            // if (typeState.getSchema().numFields() == 4) {   // filtered
            //     assertEquals(i, obj.getInt("intField"));
            // }
        }
    }
}
