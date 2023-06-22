package com.netflix.hollow.tools.history;

import com.netflix.hollow.core.AbstractStateEngineTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.tools.history.keyindex.HollowHistoryKeyIndex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class HollowHistoryRehashTest extends AbstractStateEngineTest {
    private HollowObjectSchema schema;

    @Before
    public void setUp() {
        schema = new HollowObjectSchema("A", 2);
        schema.addField("id", FieldType.FLOAT);
        schema.addField("anotherField", FieldType.LONG);

        super.setUp();
    }

    @Test
    public void correctlyRehashesKeys_beforeAndAfterSnapshot() throws IOException {
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);
        HollowHistory history = new HollowHistory(readEngine, 1L, 1);
        HollowHistoryKeyIndex keyIdx = new HollowHistoryKeyIndex(history);
        keyIdx.addTypeIndex("A", "id", "anotherField");
        keyIdx.indexTypeField("A", "id");
        keyIdx.indexTypeField("A", "anotherField");

        // Will rehash before 2069, otherwise couldn't store all values
        for(int i=0;i<5000;i++) {
            addRecord((float)i, (long)i);
        }

        roundTripSnapshot();
        keyIdx.update(readStateEngine, false);

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("A");

        // Test objects before and after rehash
        for(int ordinal=0;ordinal<5000;ordinal++) {
            Assert.assertEquals(keyIdx.getRecordKeyOrdinal(typeState, ordinal), ordinal);
            String expectedString = (float)ordinal+":"+ordinal;
            Assert.assertEquals(keyIdx.getKeyDisplayString("A", ordinal), expectedString);
        }
    }

    @Test
    public void correctlyRehashesKeys_ignoresDuplicates() throws IOException {
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);
        HollowHistory history = new HollowHistory(readEngine, 1L, 1);
        HollowHistoryKeyIndex keyIdx = new HollowHistoryKeyIndex(history);
        keyIdx.addTypeIndex("A", "id", "anotherField");
        keyIdx.indexTypeField("A", "id");
        keyIdx.indexTypeField("A", "anotherField");

        // Should ignore second ones and keep hashes of first
        for(int i=0;i<5000;i++) {
            addRecord((float)i, (long)i);
        }

        roundTripSnapshot();
        keyIdx.update(readStateEngine, false);

        for(int i=5000;i>0;i--) {
            addRecord((float)i, (long)i);
        }

        roundTripSnapshot();
        keyIdx.update(readStateEngine, false);

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("A");

        // Test objects before and after rehash
        for(int ordinal=0;ordinal<5000;ordinal++) {
            Assert.assertEquals(keyIdx.getRecordKeyOrdinal(typeState, ordinal), ordinal);
            String expectedString = (float)ordinal+":"+ordinal;
            Assert.assertEquals(keyIdx.getKeyDisplayString("A", ordinal), expectedString);
        }
    }

    @Test
    public void correctlyRehashesKeys_beforeAndAfterDelta() throws IOException {
        HollowReadStateEngine readEngine = StateEngineRoundTripper.roundTripSnapshot(writeStateEngine);
        HollowHistory history = new HollowHistory(readEngine, 1L, 1);
        HollowHistoryKeyIndex keyIdx = new HollowHistoryKeyIndex(history);
        keyIdx.addTypeIndex("A", "id", "anotherField");
        keyIdx.indexTypeField("A", "id");
        keyIdx.indexTypeField("A", "anotherField");

        // Will rehash before 2069, otherwise couldn't store all values
        for(int i=0;i<1000;i++) {
            addRecord((float)i, (long)i);
        }

        roundTripSnapshot();
        keyIdx.update(readStateEngine, false);

        for(int i=1000;i<5000;i++) {
            addRecord((float)i, (long)i);
        }

        roundTripDelta();
        keyIdx.update(readStateEngine, true);

        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readStateEngine.getTypeState("A");

        // Test objects before and after rehash
        for(int ordinal=0;ordinal<5000;ordinal++) {
            Assert.assertEquals(keyIdx.getRecordKeyOrdinal(typeState, ordinal), ordinal);
            String expectedString = (float)ordinal+":"+ordinal;
            Assert.assertEquals(keyIdx.getKeyDisplayString("A", ordinal), expectedString);
        }
    }

    @Override
    protected void initializeTypeStates() {
        HollowObjectTypeWriteState writeState = new HollowObjectTypeWriteState(schema);
        writeStateEngine.addTypeState(writeState);
    }

    private void addRecord(float id, Long secondId) {
        HollowObjectWriteRecord aRec = new HollowObjectWriteRecord(schema);
        aRec.setFloat("id", id);
        aRec.setLong("anotherField", secondId);
        writeStateEngine.add("A", aRec);
    }

}
