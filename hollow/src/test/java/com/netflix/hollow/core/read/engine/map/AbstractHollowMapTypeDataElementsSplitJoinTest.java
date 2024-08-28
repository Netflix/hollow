package com.netflix.hollow.core.read.engine.map;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.read.engine.AbstractHollowTypeDataElementsSplitJoinTest;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeDataElements;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Before;
import org.mockito.MockitoAnnotations;

public class AbstractHollowMapTypeDataElementsSplitJoinTest extends AbstractHollowTypeDataElementsSplitJoinTest {
    protected HollowMapSchema mapSchema;

    @Before
    public void setUp() {
        this.mapSchema = new HollowMapSchema("TestMap", "TestObject", "String", "intField");
        super.setUp();

        MockitoAnnotations.initMocks(this);
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

            // System.out.println(obj.toString());  // SNAP: TODO: cleanup
            // assertEquals(i, obj.get("longField"));
            // assertEquals("Value"+i, obj.getString("stringField"));
            // assertEquals((double)i, obj.getDouble("doubleField"), 0);
            // if (typeState.getSchema().numFields() == 4) {   // filtered
            //     assertEquals(i, obj.getInt("intField"));
            // }
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
