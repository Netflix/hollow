package com.netflix.hollow.core.read.engine.object;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.engine.ExperimentalFeature;
import com.netflix.hollow.core.read.engine.HollowReadConfiguration;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class HollowObjectTypeReadStateStringTest {

    @Parameterized.Parameters(name = "recycling={0}, strings={1}, shards={2}")
    public static Collection<Object[]> parameters() {
        return Arrays.asList(new Object[][] {
                { true, false, false }, { true, false, true }, { true, true, false }, { true, true, true },
                { false, false, false }, { false, false, true }, { false, true, false }, { false, true, true }
        });
    }

    private final boolean recycling;
    private final boolean strings;
    private final boolean shards;

    public HollowObjectTypeReadStateStringTest(boolean recycling, boolean strings, boolean shards) {
        this.recycling = recycling;
        this.strings = strings;
        this.shards = shards;
    }

    @Test
    public void readsAndComparesStrings() throws Exception {
        HollowWriteStateEngine writeStateEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeStateEngine);
        mapper.initializeTypeState(String.class);

        char[] longValue = new char[120];
        Arrays.fill(longValue, 'a');
        String longAscii = new String(longValue);
        String[] values = {
                "short ascii",
                "an ascii string deliberately long enough to cross small segment boundaries",
                "unicode \u123e interspersed \uffff with ascii",
                longAscii,
                longAscii.replace('a', '\u123e'),
                "\u0080 ascii after larger strings"
        };
        int[] ordinals = new int[values.length];
        for(int i = 0; i < values.length; i++)
            ordinals[i] = mapper.add(values[i]);

        ArraySegmentRecycler recycler = recycling
                ? new RecyclingRecycler(5, 2)
                : new WastefulRecycler(5, 2);
        EnumSet<ExperimentalFeature> features = EnumSet.noneOf(ExperimentalFeature.class);
        if(strings)
            features.add(ExperimentalFeature.DIRECT_SEGMENT_STRING_READS);
        if(shards)
            features.add(ExperimentalFeature.SHARD_READ_FAST_PATHS);
        HollowReadStateEngine readStateEngine = new HollowReadStateEngine(new HollowReadConfiguration(MemoryMode.ON_HEAP, recycler,
                features.toArray(new ExperimentalFeature[0])));
        StateEngineRoundTripper.roundTripSnapshot(writeStateEngine, readStateEngine, null);

        HollowObjectTypeReadState dataAccess = (HollowObjectTypeReadState)readStateEngine.getTypeState("String");
        HollowObjectTypeDataElements data = dataAccess.currentDataElements()[0];
        TrackingStringData tracking = new TrackingStringData(recycler, (SegmentedByteArray)data.varLengthData[0]);
        data.varLengthData[0] = tracking;
        for(int i = 0; i < values.length; i++) {
            assertEquals(values[i], dataAccess.readString(ordinals[i], 0));
            assertTrue(dataAccess.isStringFieldEqual(ordinals[i], 0, values[i]));
            assertFalse(dataAccess.isStringFieldEqual(ordinals[i], 0, values[i] + "x"));
        }
        assertEquals(strings && !recycling ? values.length : 0, tracking.decodes);
        assertEquals(strings && !recycling ? values.length * 2 : 0, tracking.comparisons);
    }

    private static final class TrackingStringData extends SegmentedByteArray {
        private final SegmentedByteArray delegate;
        private int decodes;
        private int comparisons;

        TrackingStringData(ArraySegmentRecycler recycler, SegmentedByteArray delegate) {
            super(recycler);
            this.delegate = delegate;
        }

        @Override
        public byte get(long index) {
            return delegate.get(index);
        }

        @Override
        public String readVIntString(long position, int length, char[] output) {
            decodes++;
            return delegate.readVIntString(position, length, output);
        }

        @Override
        public boolean isVIntStringEqual(long position, int length, String value) {
            comparisons++;
            return delegate.isVIntStringEqual(position, length, value);
        }
    }
}
