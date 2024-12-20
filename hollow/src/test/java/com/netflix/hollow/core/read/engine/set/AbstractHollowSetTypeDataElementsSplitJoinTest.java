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
    protected HollowSetTypeReadState mockSetTypeState;

    @Before
    public void setUp() {
        this.setSchema = new HollowSetSchema("TestSet", "TestObject");

        super.setUp();

        MockitoAnnotations.initMocks(this);
        HollowSetTypeDataElements[] fakeDataElements = new HollowSetTypeDataElements[5];
        when(mockSetTypeState.currentDataElements()).thenReturn(fakeDataElements);
    }

    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.addTypeState(new HollowSetTypeWriteState(setSchema));
        writeStateEngine.setTargetMaxTypeShardSize(4 * 100 * 1000 * 1024);
    }

    int[][] generateSetContents(int numRecords) {
        int[][] setContents = new int[numRecords][];
        for (int i=0;i<numRecords;i++) {
            setContents[i] = new int[i+1];
            for (int j=0;j<i+1;j++) {
                setContents[i][j] = j;
            }
        }
        return setContents;
    }

    protected HollowSetTypeReadState populateTypeStateWith(int[][] setContents) throws IOException {
        int numOrdinals = 1 + Arrays.stream(setContents)
                .flatMapToInt(Arrays::stream)
                .max()
                .orElse(0);
        // populate write state with that many ordinals
        super.populateWriteStateEngine(numOrdinals);
        for(int[] set : setContents) {
            HollowSetWriteRecord rec = new HollowSetWriteRecord();
            for(int ordinal : set) {
                rec.addElement(ordinal);
            }
            writeStateEngine.add("TestSet", rec);
        }
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
        }
    }
}
