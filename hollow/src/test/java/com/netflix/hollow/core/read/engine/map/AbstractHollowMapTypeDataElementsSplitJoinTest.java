package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class AbstractHollowMapTypeDataElementsSplitJoinTest extends AbstractHollowTypeDataElementsSplitJoinTest {
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

    protected void populateWriteStateEngine(HollowWriteStateEngine writeStateEngine, int[][][] maps) {
        // populate state so that there is 1:1 correspondence in key/value ordinal to value in int type
        // find max value across all maps
        if (maps.length > 0) {
            int numKeyValueOrdinals = 1 + Arrays.stream(maps)
                    .flatMap(Arrays::stream)
                    .flatMapToInt(Arrays::stream)
                    .max()
                    .orElseThrow(() -> new IllegalArgumentException("Array is empty"));

            // populate write state with that many ordinals
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
            for(int i=0;i<numKeyValueOrdinals;i++) {
                rec.reset();
                rec.setLong("longField", i);
                rec.setString("stringField", "Value" + i);
                rec.setInt("intField", i);
                rec.setDouble("doubleField", i);

                writeStateEngine.add("TestObject", rec);
            }
        }
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
        populateWriteStateEngine(writeStateEngine, maps);
        roundTripSnapshot();
        return (HollowMapTypeReadState) readStateEngine.getTypeState("TestMap");
    }

    protected int[][][] generateMapContents(int numRecords) {
        int[][][] maps = new int[numRecords][][];
        Random random = new Random();
        int maxEntries = 10;
        for (int i=0;i<numRecords;i++) {
            int numEntries = 1 + random.nextInt(maxEntries);
            maps[i] = new int[numEntries][2];
            for (int j=0;j<numEntries;j++) {
                maps[i][j][0] = (i * maxEntries) + j;
                maps[i][j][1] = (i * maxEntries) + j + 1;
            }
        }
        return maps;
    }

    protected void assertDataUnchanged(HollowMapTypeReadState typeState, int[][][] maps) {
        int numMapRecords = maps.length;
        for(int i=0;i<numMapRecords;i++) {
            Map<Integer, Integer> expected = convertToMap(maps[i]);
            boolean matched = false;
            for (int mapRecordOridnal=0; mapRecordOridnal<=typeState.maxOrdinal(); mapRecordOridnal++) {
                Map<Integer, Integer> actual = new HashMap<>();
                HollowMapEntryOrdinalIterator iter = typeState.ordinalIterator(mapRecordOridnal);
                boolean hasMore = iter.next();
                while (hasMore) {
                    int key = iter.getKey();
                    int value = iter.getValue();
                    actual.put(key, value);
                    hasMore = iter.next();
                }
                if (actual.equals(expected)) {
                    matched = true;
                    break;
                }
            }
            assertTrue(matched);
        }
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
