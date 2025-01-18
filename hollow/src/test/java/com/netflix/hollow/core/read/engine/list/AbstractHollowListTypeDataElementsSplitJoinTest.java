package com.netflix.hollow.core.read.engine.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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

    protected int[][] generateListContents(int numRecords) {
        int[][] listContents = new int[numRecords][];
        for (int i=0;i<numRecords;i++) {
            listContents[i] = new int[i+1];
            for (int j=0;j<i+1;j++) {
                listContents[i][j] = j;
            }
        }
        return listContents;
    }

    protected void assertDataUnchanged(HollowListTypeReadState typeState, int[][] listContents) {
        int numListRecords = listContents.length;
        if (typeState.getListener(PopulatedOrdinalListener.class) != null) {
            assertEquals(listContents.length, typeState.getPopulatedOrdinals().cardinality());
        }
        for(int i=0;i<numListRecords;i++) {
            List<Integer> expected = Arrays.stream(listContents[i]).boxed().collect(Collectors.toList());
            boolean matched = false;
            List<Integer> actual = null;
            for (int listRecordOridnal=0; listRecordOridnal<=typeState.maxOrdinal(); listRecordOridnal++) {
                HollowOrdinalIterator iter = typeState.ordinalIterator(listRecordOridnal);
                actual = new ArrayList<>();
                int o = iter.next();
                while (o != HollowOrdinalIterator.NO_MORE_ORDINALS) {
                    actual.add(o);
                    o = iter.next();
                }
                if (actual.equals(expected)) {
                    matched = true;
                    break;
                }
            }
            assertTrue(matched);
        }
    }
}
