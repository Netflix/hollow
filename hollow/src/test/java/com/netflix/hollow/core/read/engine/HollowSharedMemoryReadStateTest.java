/*
 *  Copyright 2026 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
package com.netflix.hollow.core.read.engine;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.list.HollowListTypeReadState;
import com.netflix.hollow.core.read.engine.map.HollowMapTypeReadState;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.engine.set.HollowSetTypeReadState;
import com.netflix.hollow.core.read.iterator.HollowMapEntryOrdinalIterator;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowListTypeWriteState;
import com.netflix.hollow.core.write.HollowListWriteRecord;
import com.netflix.hollow.core.write.HollowMapTypeWriteState;
import com.netflix.hollow.core.write.HollowMapWriteRecord;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowSetTypeWriteState;
import com.netflix.hollow.core.write.HollowSetWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class HollowSharedMemoryReadStateTest {

    @Parameters(name = "recycling={0}, fastPaths={1}")
    public static Collection<Object[]> recyclerModes() {
        return Arrays.asList(new Object[][] { { true, true }, { true, false }, { false, true }, { false, false } });
    }

    private final boolean recycling;
    private final boolean fastPaths;

    public HollowSharedMemoryReadStateTest(boolean recycling, boolean fastPaths) {
        this.recycling = recycling;
        this.fastPaths = fastPaths;
    }

    @Test
    public void readsObjectsAndCollectionsFromImmutableMappedShards() throws IOException {
        String[] strings = { "ascii", "mixed \u1234 text \ud83d\ude00", null };
        byte[][] bytes = { { 1, 2, 3 }, new byte[0], null };
        HollowReadStateEngine stateEngine = readSnapshot(strings, bytes);

        assertImmutableShards(stateEngine);
        assertObjects(stateEngine, strings, bytes);
        assertCollections(stateEngine);
    }

    @Test
    public void readsEmptyAndNullVariableLengthValuesFromMappedShards() throws IOException {
        String[] strings = { "", null, "" };
        byte[][] bytes = { new byte[0], null, new byte[0] };
        HollowReadStateEngine stateEngine = readSnapshot(strings, bytes);

        assertImmutableShards(stateEngine);
        assertObjects(stateEngine, strings, bytes);
        assertCollections(stateEngine);
    }

    private HollowReadStateEngine readSnapshot(String[] strings, byte[][] bytes) throws IOException {
        HollowObjectSchema objectSchema = new HollowObjectSchema("Object", 3);
        objectSchema.addField("long", HollowObjectSchema.FieldType.LONG);
        objectSchema.addField("string", HollowObjectSchema.FieldType.STRING);
        objectSchema.addField("bytes", HollowObjectSchema.FieldType.BYTES);
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.addTypeState(new HollowObjectTypeWriteState(objectSchema, 1));
        writeEngine.addTypeState(new HollowListTypeWriteState(new HollowListSchema("List", "Object"), 1));
        writeEngine.addTypeState(new HollowSetTypeWriteState(new HollowSetSchema("Set", "Object"), 1));
        writeEngine.addTypeState(new HollowMapTypeWriteState(new HollowMapSchema("Map", "Object", "Object"), 1));

        for(int i = 0; i < strings.length; i++) {
            HollowObjectWriteRecord record = new HollowObjectWriteRecord(objectSchema);
            record.setLong("long", i == 2 ? Long.MAX_VALUE : i - 1);
            record.setString("string", strings[i]);
            record.setBytes("bytes", bytes[i]);
            assertEquals(i, writeEngine.add("Object", record));
        }

        HollowListWriteRecord list = new HollowListWriteRecord();
        list.addElement(0);
        list.addElement(2);
        list.addElement(1);
        assertEquals(0, writeEngine.add("List", list));
        assertEquals(1, writeEngine.add("List", new HollowListWriteRecord()));

        HollowSetWriteRecord set = new HollowSetWriteRecord();
        for(int i = 0; i < strings.length; i++)
            set.addElement(i);
        assertEquals(0, writeEngine.add("Set", set));
        assertEquals(1, writeEngine.add("Set", new HollowSetWriteRecord()));

        HollowMapWriteRecord map = new HollowMapWriteRecord();
        map.addEntry(0, 2);
        map.addEntry(1, 0);
        map.addEntry(2, 1);
        assertEquals(0, writeEngine.add("Map", map));
        assertEquals(1, writeEngine.add("Map", new HollowMapWriteRecord()));

        File file = File.createTempFile("shared-memory-read-state", ".bin");
        file.deleteOnExit();
        try(FileOutputStream out = new FileOutputStream(file)) {
            new HollowBlobWriter(writeEngine).writeSnapshot(out);
        }
        HollowReadStateEngine stateEngine = new HollowReadStateEngine(new HollowReadConfiguration(MemoryMode.SHARED_MEMORY_LAZY,
                recycling ? new RecyclingRecycler(5, 2) : new WastefulRecycler(5, 2), fastPaths
                ? new ExperimentalFeature[] { ExperimentalFeature.SHARD_READ_FAST_PATHS } : new ExperimentalFeature[0]));
        HollowBlobReader reader = new HollowBlobReader(stateEngine, MemoryMode.SHARED_MEMORY_LAZY);
        // Small mappings exercise reads across mapped-buffer boundaries.
        try(HollowBlobInput in = HollowBlobInput.randomAccess(file, 16)) {
            reader.readSnapshot(in);
        }
        return stateEngine;
    }

    private static void assertImmutableShards(HollowReadStateEngine stateEngine) {
        assertEquals(4, stateEngine.getTypeStates().size());
        for(HollowTypeReadState typeState : stateEngine.getTypeStates())
            assertTrue(typeState.getSchema().getName(), typeState.shardsAreImmutable);
    }

    private static void assertObjects(HollowReadStateEngine stateEngine, String[] strings, byte[][] bytes) {
        HollowObjectTypeReadState objects = (HollowObjectTypeReadState)stateEngine.getTypeState("Object");
        for(int i = 0; i < strings.length; i++) {
            assertEquals(i == 2 ? Long.MAX_VALUE : i - 1, objects.readLong(i, 0));
            assertEquals(strings[i], objects.readString(i, 1));
            assertTrue(objects.isStringFieldEqual(i, 1, strings[i]));
            assertFalse(objects.isStringFieldEqual(i, 1, strings[i] == null ? "x" : strings[i] + "x"));
            assertArrayEquals(bytes[i], objects.readBytes(i, 2));
        }
    }

    private static void assertCollections(HollowReadStateEngine stateEngine) {
        HollowListTypeReadState lists = (HollowListTypeReadState)stateEngine.getTypeState("List");
        assertEquals(3, lists.size(0));
        int[] expectedList = { 0, 2, 1 };
        HollowOrdinalIterator listIterator = lists.ordinalIterator(0);
        for(int i = 0; i < expectedList.length; i++) {
            assertEquals(expectedList[i], lists.getElementOrdinal(0, i));
            assertEquals(expectedList[i], listIterator.next());
        }
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, listIterator.next());
        assertEquals(0, lists.size(1));
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, lists.ordinalIterator(1).next());

        HollowSetTypeReadState sets = (HollowSetTypeReadState)stateEngine.getTypeState("Set");
        assertEquals(3, sets.size(0));
        for(int i = 0; i < 3; i++)
            assertTrue(sets.contains(0, i));
        HollowOrdinalIterator setIterator = sets.ordinalIterator(0);
        BitSet elements = new BitSet();
        for(int ordinal = setIterator.next(); ordinal != HollowOrdinalIterator.NO_MORE_ORDINALS;
                ordinal = setIterator.next())
            elements.set(ordinal);
        BitSet expectedSet = new BitSet();
        expectedSet.set(0, 3);
        assertEquals(expectedSet, elements);
        assertEquals(0, sets.size(1));
        assertFalse(sets.contains(1, 0));
        assertEquals(HollowOrdinalIterator.NO_MORE_ORDINALS, sets.ordinalIterator(1).next());

        HollowMapTypeReadState maps = (HollowMapTypeReadState)stateEngine.getTypeState("Map");
        assertEquals(3, maps.size(0));
        int[] expectedValues = { 2, 0, 1 };
        for(int i = 0; i < expectedValues.length; i++)
            assertEquals(expectedValues[i], maps.get(0, i));
        HollowMapEntryOrdinalIterator mapIterator = maps.ordinalIterator(0);
        int entries = 0;
        while(mapIterator.next()) {
            assertEquals(expectedValues[mapIterator.getKey()], mapIterator.getValue());
            entries++;
        }
        assertEquals(3, entries);
        assertEquals(0, maps.size(1));
        assertEquals(-1, maps.get(1, 0));
        assertFalse(maps.ordinalIterator(1).next());
    }
}
