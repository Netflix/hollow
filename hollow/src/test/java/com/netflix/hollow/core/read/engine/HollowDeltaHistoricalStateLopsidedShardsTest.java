package com.netflix.hollow.core.read.engine;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.core.read.engine.list.HollowListDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetDeltaHistoricalStateCreator;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowTypeWriteState;
import com.netflix.hollow.core.write.HollowWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;
import org.junit.Test;

/**
 * Object longevity copies the records removed by a delta into a single-shard historical state. A consumer that
 * skips type shards with no additions leaves those shards at the element widths of an earlier state, so the shards
 * of one type can differ in width when their removed records are copied.
 */
public class HollowDeltaHistoricalStateLopsidedShardsTest {

    private static final String ELEM = "Elem";
    private static final String TYPE = "Container";

    // Cycle 1: Elem ordinals 0..2, container ordinals 0..4 split across 2 shards (ordinal & 1).
    // Cycle 2: Elem ordinals up to 10 widen the referencing field, container 0 (shard 0) is removed and container 5
    // (shard 1) is added. Shard 0 has removals only, so it keeps the narrower width when skipped.
    private static final int[][] CONTENTS = {{1, 2, 0}, {0}, {1}, {2}, {0, 1}, {10}};

    private interface RecordWriter {
        HollowWriteRecord record(HollowWriteStateEngine writeEngine, int[] elems, int idx);
    }

    @Test
    public void listRecordRemovedFromNarrowerShard() throws IOException {
        assertRemovedRecordIsPreserved(() -> new HollowListTypeWriteState(new HollowListSchema(TYPE, ELEM), 2), (w, e, idx) -> {
            HollowListWriteRecord rec = new HollowListWriteRecord();
            for (int x : CONTENTS[idx])
                rec.addElement(e[x]);
            return rec;
        });
    }

    @Test
    public void setRecordRemovedFromNarrowerShard() throws IOException {
        assertRemovedRecordIsPreserved(() -> new HollowSetTypeWriteState(new HollowSetSchema(TYPE, ELEM), 2), (w, e, idx) -> {
            HollowSetWriteRecord rec = new HollowSetWriteRecord();
            for (int x : CONTENTS[idx])
                rec.addElement(e[x]);
            return rec;
        });
    }

    @Test
    public void mapRecordRemovedFromNarrowerShard() throws IOException {
        assertRemovedRecordIsPreserved(() -> new HollowMapTypeWriteState(new HollowMapSchema(TYPE, ELEM, ELEM), 2), (w, e, idx) -> {
            HollowMapWriteRecord rec = new HollowMapWriteRecord();
            if (idx == 0) {
                rec.addEntry(e[1], e[2]);
                rec.addEntry(e[2], e[0]);
            } else {
                int[] c = CONTENTS[idx];
                rec.addEntry(e[c[0]], e[c[c.length - 1]]);
            }
            return rec;
        });
    }

    @Test
    public void objectRecordRemovedFromNarrowerShard() throws IOException {
        assertRemovedRecordIsPreserved(() -> {
            HollowObjectSchema schema = new HollowObjectSchema(TYPE, 2);
            schema.addField("ref", FieldType.REFERENCE, ELEM);
            schema.addField("num", FieldType.INT);
            return new HollowObjectTypeWriteState(schema, 2);
        }, (w, e, idx) -> {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) w.getSchema(TYPE));
            rec.setReference("ref", e[CONTENTS[idx][0]]);
            if (idx != 0)   // null in the removed record, widened by record 5
                rec.setInt("num", idx == 5 ? 1000 : idx);
            return rec;
        });
    }

    @Test
    public void mapKeyAndValueWidthsPeakInDifferentShards() throws IOException {
        HollowWriteStateEngine w = newWriteEngine(new HollowMapTypeWriteState(new HollowMapSchema(TYPE, ELEM, ELEM), 2));
        HollowReadStateEngine r = new HollowReadStateEngine();
        r.setSkipTypeShardUpdateWithNoAdditions(true);

        // cycle 1: the map at ordinal 1 (shard 1) holds the only value at Elem 9, so keys take 2 bits and values 4
        int[] e = addElems(w, 10);
        assertEquals(0, w.add(TYPE, map(e[1], e[2], e[2], e[0])));
        assertEquals(1, w.add(TYPE, map(e[0], e[9])));
        assertEquals(2, w.add(TYPE, map(e[2], e[1])));
        assertEquals(3, w.add(TYPE, map(e[1], e[1])));
        assertEquals(4, w.add(TYPE, map(e[2], e[2])));
        StateEngineRoundTripper.roundTripSnapshot(w, r);
        String removed = read(r.getTypeState(TYPE), 0);

        // cycle 2: ordinal 1 removed, no additions, so both shards keep their widths
        w.prepareForNextCycle();
        e = addElems(w, 10);
        w.add(TYPE, map(e[1], e[2], e[2], e[0]));
        w.add(TYPE, map(e[2], e[1]));
        w.add(TYPE, map(e[1], e[1]));
        w.add(TYPE, map(e[2], e[2]));
        StateEngineRoundTripper.roundTripDelta(w, r);

        // cycle 3: values shrink to 2 bits while a key at Elem 9 grows keys to 4 bits in shard 1. Shard 0 only loses
        // ordinal 0, so it stays at key 2 + value 4 against key 4 + value 2 in shard 1; the copy needs 4 + 4.
        w.prepareForNextCycle();
        e = addElems(w, 10);
        w.add(TYPE, map(e[2], e[1]));
        w.add(TYPE, map(e[1], e[1]));
        w.add(TYPE, map(e[2], e[2]));
        assertEquals(1, w.add(TYPE, map(e[9], e[1])));
        StateEngineRoundTripper.roundTripDelta(w, r);

        assertEquals(removed, readHistorical(r.getTypeState(TYPE), 0));
    }

    private static void assertRemovedRecordIsPreserved(Supplier<HollowTypeWriteState> typeState, RecordWriter writer) throws IOException {
        for (boolean skipShardsWithNoAdditions : new boolean[] {false, true}) {
            HollowWriteStateEngine w = newWriteEngine(typeState.get());
            HollowReadStateEngine r = new HollowReadStateEngine();
            r.setSkipTypeShardUpdateWithNoAdditions(skipShardsWithNoAdditions);

            int[] e = addElems(w, 3);
            for (int i = 0; i < 5; i++)
                assertEquals(i, w.add(TYPE, writer.record(w, e, i)));
            StateEngineRoundTripper.roundTripSnapshot(w, r);
            String removed = read(r.getTypeState(TYPE), 0);

            w.prepareForNextCycle();
            e = addElems(w, 11);
            for (int i = 1; i < 6; i++)
                assertEquals(i, w.add(TYPE, writer.record(w, e, i)));
            StateEngineRoundTripper.roundTripDelta(w, r);

            assertEquals("skipTypeShardUpdateWithNoAdditions=" + skipShardsWithNoAdditions,
                    removed, readHistorical(r.getTypeState(TYPE), 0));
        }
    }

    private static HollowWriteStateEngine newWriteEngine(HollowTypeWriteState typeState) {
        HollowWriteStateEngine w = new HollowWriteStateEngine();
        HollowObjectSchema elemSchema = new HollowObjectSchema(ELEM, 1);
        elemSchema.addField("id", FieldType.INT);
        w.addTypeState(new HollowObjectTypeWriteState(elemSchema));
        w.addTypeState(typeState);
        return w;
    }

    private static int[] addElems(HollowWriteStateEngine w, int count) {
        int[] ordinals = new int[count];
        for (int i = 0; i < count; i++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) w.getSchema(ELEM));
            rec.setInt("id", i);
            ordinals[i] = w.add(ELEM, rec);
        }
        return ordinals;
    }

    private static HollowMapWriteRecord map(int... keyValuePairs) {
        HollowMapWriteRecord rec = new HollowMapWriteRecord();
        for (int i = 0; i < keyValuePairs.length; i += 2)
            rec.addEntry(keyValuePairs[i], keyValuePairs[i + 1]);
        return rec;
    }

    private static String read(HollowTypeReadState typeState, int ordinal) {
        if (typeState instanceof HollowListTypeReadState) {
            HollowListTypeReadState list = (HollowListTypeReadState) typeState;
            List<Integer> elements = new ArrayList<>();
            for (int i = 0; i < list.size(ordinal); i++)
                elements.add(list.getElementOrdinal(ordinal, i));
            return elements.toString();
        } else if (typeState instanceof HollowSetTypeReadState) {
            HollowSetTypeReadState set = (HollowSetTypeReadState) typeState;
            TreeSet<Integer> elements = new TreeSet<>();
            HollowOrdinalIterator iter = set.ordinalIterator(ordinal);
            for (int element = iter.next(); element != HollowOrdinalIterator.NO_MORE_ORDINALS; element = iter.next())
                elements.add(element);
            return set.size(ordinal) + " " + elements;
        } else if (typeState instanceof HollowMapTypeReadState) {
            HollowMapTypeReadState map = (HollowMapTypeReadState) typeState;
            TreeMap<Integer, Integer> entries = new TreeMap<>();
            HollowMapEntryOrdinalIterator iter = map.ordinalIterator(ordinal);
            while (iter.next())
                entries.put(iter.getKey(), iter.getValue());
            return map.size(ordinal) + " " + entries;
        }
        HollowObjectTypeReadState object = (HollowObjectTypeReadState) typeState;
        return Arrays.asList(object.readOrdinal(ordinal, 0), object.readInt(ordinal, 1)).toString();
    }

    private static String readHistorical(HollowTypeReadState typeState, int removedOrdinal) {
        if (typeState instanceof HollowListTypeReadState) {
            HollowListDeltaHistoricalStateCreator creator = new HollowListDeltaHistoricalStateCreator((HollowListTypeReadState) typeState, false);
            creator.populateHistory();
            return read(creator.createHistoricalTypeReadState(), creator.getOrdinalMapping().get(removedOrdinal));
        } else if (typeState instanceof HollowSetTypeReadState) {
            HollowSetDeltaHistoricalStateCreator creator = new HollowSetDeltaHistoricalStateCreator((HollowSetTypeReadState) typeState, false);
            creator.populateHistory();
            return read(creator.createHistoricalTypeReadState(), creator.getOrdinalMapping().get(removedOrdinal));
        } else if (typeState instanceof HollowMapTypeReadState) {
            HollowMapDeltaHistoricalStateCreator creator = new HollowMapDeltaHistoricalStateCreator((HollowMapTypeReadState) typeState, false);
            creator.populateHistory();
            return read(creator.createHistoricalTypeReadState(), creator.getOrdinalMapping().get(removedOrdinal));
        }
        HollowObjectDeltaHistoricalStateCreator creator = new HollowObjectDeltaHistoricalStateCreator((HollowObjectTypeReadState) typeState, false);
        creator.populateHistory();
        return read(creator.createHistoricalTypeReadState(), creator.getOrdinalMapping().get(removedOrdinal));
    }
}
