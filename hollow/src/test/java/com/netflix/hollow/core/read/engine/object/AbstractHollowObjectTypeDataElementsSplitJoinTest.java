package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import java.io.IOException;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowObjectTypeDataElementsSplitJoinTest extends AbstractHollowTypeDataElementsSplitJoinTest {

    @Mock
    protected HollowObjectTypeReadState mockObjectTypeState;

    @Before
    public void setUp() {
        super.setUp();

        MockitoAnnotations.initMocks(this);
        HollowObjectTypeDataElements[] fakeDataElements = new HollowObjectTypeDataElements[5];
        when(mockObjectTypeState.currentDataElements()).thenReturn(fakeDataElements);
    }

    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.setTargetMaxTypeShardSize(4 * 1000 * 1024);
    }

    protected HollowObjectTypeReadState populateTypeStateWithRepro() throws IOException {
        populateWriteStateEngineWithRepro();
        roundTripSnapshot();
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected HollowObjectTypeReadState populateTypeStateWith(int numRecords) throws IOException {
        populateWriteStateEngine(numRecords);
        roundTripSnapshot();
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected HollowObjectTypeReadState populateTypeStateWith(int[] recordIds) throws IOException {
        populateWriteStateEngine(recordIds);
        roundTripSnapshot();
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected HollowObjectTypeReadState populateTypeStateWithFilter(int numRecords) throws IOException {
        populateWriteStateEngine(numRecords);
        readStateEngine = new HollowReadStateEngine();
        HollowFilterConfig readFilter = new HollowFilterConfig(true);
        readFilter.addField("TestObject", "intField");
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, readFilter);
        return (HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject");
    }

    protected void assertDataUnchanged(int numRecords) {
        assertDataUnchanged((HollowObjectTypeReadState) readStateEngine.getTypeState("TestObject"), numRecords);
    }

    protected void assertDataUnchanged(HollowObjectTypeReadState typeState, int numRecords) {
        for(int i=0;i<numRecords;i++) {
            GenericHollowObject obj = new GenericHollowObject(typeState, i);
            assertEquals(i, obj.getLong("longField"));
            assertEquals("Value"+i, obj.getString("stringField"));
            assertEquals((double)i, obj.getDouble("doubleField"), 0);
            if (typeState.getSchema().numFields() == 4) {   // filtered
                assertEquals(i, obj.getInt("intField"));
            }
        }
    }
}
