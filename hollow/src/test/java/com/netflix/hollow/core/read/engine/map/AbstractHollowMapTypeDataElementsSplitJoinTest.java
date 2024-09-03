package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.HollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowMapTypeDataElementsSplitJoinTest extends HollowTypeDataElementsSplitJoinTest {
    protected HollowMapSchema mapSchema;

    @Mock
    protected HollowMapTypeReadState mockMapTypeState;

    @Before
    public void setUp() {
        this.mapSchema = new HollowMapSchema("TestMap", "TestObject", "String", "intField");
        super.setUp();

        MockitoAnnotations.initMocks(this);
        HollowMapTypeDataElements[] fakeDataElements = new HollowMapTypeDataElements[5];
        when(mockMapTypeState.currentDataElements()).thenReturn(fakeDataElements);
    }

    @Override
    protected void initializeTypeStates() {
        super.initializeTypeStates();
        writeStateEngine.addTypeState(new HollowMapTypeWriteState(mapSchema));
        writeStateEngine.setTargetMaxTypeShardSize(4 * 100 * 1000 * 1024);
    }

    private void populateWriteStateEngine(int[][][] maps) {
        // populate state so that there is 1:1 correspondence in key/value ordinal to value in int type
        // find max value across all maps
        int numKeyValueOrdinals = 1 + Arrays.stream(maps)
                .flatMap(Arrays::stream)
                .flatMapToInt(Arrays::stream)
                .max()
                .orElseThrow(() -> new IllegalArgumentException("Array is empty"));
        // populate write state with that many ordinals
        super.populateWriteStateEngine(numKeyValueOrdinals);
        for(int[][] map : maps) {
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            for (int[] entry : map) {
                assertEquals(2, entry.length);    // key value pair
                rec.addEntry(entry[0], entry[1]);
            }
            writeStateEngine.add("TestMap", rec);
        }
    }

    protected HollowMapTypeReadState populateTypeStateWith(int[][][] maps) throws IOException {
        populateWriteStateEngine(maps);
        roundTripSnapshot();
        return (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");
    }

    protected void assertDataUnchanged(HollowMapTypeReadState typeState, int[][][] maps) {
        int numMapRecords = maps.length;
        for(int i=0;i<numMapRecords;i++) {
            Map<Integer, Integer> expected = convertToMap(maps[i]);
            Map<Integer, Integer> actual = readMap(typeState, i);
            assertEquals(expected, actual);
        }
    }

    public static Map<Integer, Integer> readMap(HollowMapTypeReadState typeState, int ordinal) {
        Map<Integer, Integer> result = new HashMap<>();
        HollowMapEntryOrdinalIterator iter = typeState.ordinalIterator(ordinal);
        boolean hasMore = iter.next();
        while (hasMore) {
            int key = iter.getKey();
            int value = iter.getValue();
            result.put(key, value);
            hasMore = iter.next();
        }
        return result;
    }

    public static Map<Integer, Integer> convertToMap(int[][] array) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int[] pair : array) {
            if (pair.length == 2) {
                map.put(pair[0], pair[1]);
            } else {
                throw new IllegalArgumentException("Each sub-array must have exactly 2 elements.");
            }
        }

        return map;
    }
}
