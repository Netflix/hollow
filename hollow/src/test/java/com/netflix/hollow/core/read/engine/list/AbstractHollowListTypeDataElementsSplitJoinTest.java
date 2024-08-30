package com.netflix.hollow.core.read.engine.list;

import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowListTypeDataElementsSplitJoinTest extends AbstractHollowTypeDataElementsSplitJoinTest {
    protected HollowListSchema listSchema;

    @Mock
    protected HollowListTypeReadState mockListTypeState;

    @Before
    public void setUp() {
        this.listSchema = new HollowListSchema("TestList", "TestObject");

        super.setUp();

        MockitoAnnotations.initMocks(this);
        HollowListTypeDataElements[] fakeDataElements = new HollowListTypeDataElements[5];
        when(mockListTypeState.currentDataElements()).thenReturn(fakeDataElements);
    }

    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.addTypeState(new HollowListTypeWriteState(listSchema));
        writeStateEngine.setTargetMaxTypeShardSize(4 * 100 * 1000 * 1024);
    }

    private void populateWriteStateEngine(int[][] listContents) {
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

    protected HollowListTypeReadState populateTypeStateWith(int[][] listContents) throws IOException {
        for (int[] list : listContents) {
            populateWriteStateEngine(list);
        }
        populateWriteStateEngine(listContents);
        roundTripSnapshot();
        return (HollowListTypeReadState) readStateEngine.getTypeState("TestList");
    }

    protected void assertDataUnchanged(HollowListTypeReadState typeState, int[][] listContents) {
        int numListRecords = listContents.length;
        for(int i=0;i<numListRecords;i++) {
            HollowOrdinalIterator iter = typeState.ordinalIterator(i);
            for(int j=0;j<listContents[i].length;j++) {
                Assert.assertEquals(listContents[i][j], iter.next());
            }
            Assert.assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, iter.next());
        }
    }
}
