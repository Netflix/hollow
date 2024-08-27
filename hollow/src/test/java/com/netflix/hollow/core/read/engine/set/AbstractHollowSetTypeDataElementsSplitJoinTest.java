package com.netflix.hollow.core.read.engine.set;

import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowSetTypeDataElementsSplitJoinTest extends AbstractHollowTypeDataElementsSplitJoinTest {
    protected HollowSetSchema setSchema;

    @Mock
    protected HollowSetTypeReadState mockListTypeState;

    @Before
    public void setUp() {
        this.setSchema = new HollowSetSchema("TestSet", "TestObject");

        super.setUp();

        MockitoAnnotations.initMocks(this);
        HollowSetTypeDataElements[] fakeDataElements = new HollowSetTypeDataElements[5];
        when(mockListTypeState.currentDataElements()).thenReturn(fakeDataElements);
    }

    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(setSchema));
        writeStateEngine.setTargetMaxTypeShardSize(4 * 100 * 1000 * 1024);
    }

    private void populateWriteStateEngine(int[][] setContents) {
        for(int[] set : setContents) {
            addRecord(Arrays.stream(set).toArray());
        }
    }

    private void addRecord(int... ordinals) {
        HollowSetWriteRecord rec = new HollowSetWriteRecord();

        for(int i=0;i<ordinals.length;i++) {
            rec.addElement(ordinals[i]);
        }

        writeStateEngine.add("TestSet", rec);
    }

    protected HollowSetTypeReadState populateTypeStateWith(int[][] setContents) throws IOException {
        for(int[] set : setContents) {
            addRecord(Arrays.stream(set).toArray());
        }
        populateWriteStateEngine(setContents);
        roundTripSnapshot();
        return (HollowSetTypeReadState) readStateEngine.getTypeState("TestSet");
    }

    protected void assertDataUnchanged(HollowSetTypeReadState typeState, int[][] listContents) {
        int numListRecords = listContents.length;
        for(int i=0;i<numListRecords;i++) {
            HollowOrdinalIterator iter = typeState.ordinalIterator(i);
            Set<Integer> expected = Arrays.stream(listContents[i]).boxed().collect(Collectors.toSet());
            Set<Integer> actual = new HashSet<>();
            int o = iter.next();
            while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                actual.add(o);
                o = iter.next();
            }

            Assert.assertEquals(expected, actual);
            Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
            // System.out.println(obj.toString());  // SNAP: TODO: cleanup
            // assertEquals(i, obj.get("longField"));
            // assertEquals("Value"+i, obj.getString("stringField"));
            // assertEquals((double)i, obj.getDouble("doubleField"), 0);
            // if (typeState.getSchema().numFields() == 4) {   // filtered
            //     assertEquals(i, obj.getInt("intField"));
            // }
        }
    }
}
