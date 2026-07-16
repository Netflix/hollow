/*
 *  Copyright 2016-2019 Netflix, Inc.
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

import static com.netflix.hollow.core.read.filter.TypeFilter.newTypeFilter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.netflix.hollow.api.objects.HollowRecord;
import com.netflix.hollow.api.objects.generic.GenericHollowMap;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.filter.HollowFilterConfig;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;
import com.netflix.hollow.tools.filter.FilteredHollowBlobWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class HollowBlobReaderMissingReferencedTypeTest {

    @Test
    public void readsSnapshotWithRecursiveFilterWhenReferencedTypeSchemaIsAbsent() throws IOException {
        // 1. Produce a normal snapshot where Parent references Child, and both types have records.
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.add(new Parent(1, new Child(11)));
        mapper.add(new Parent(2, new Child(22)));

        ByteArrayOutputStream snapshot = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(snapshot);

        // 2. Filter-write a blob that DROPS the Child type from the header but keeps Parent (whose
        //    'child' field still references the now-absent Child) -- the shape RawHollow emits for an
        //    empty nested type. getFilteredSchemaList does not enforce referential closure, so the
        //    resulting header legitimately omits a referenced type.
        HollowFilterConfig excludeChild = new HollowFilterConfig(true); // exclude filter
        excludeChild.addType("Child");
        ByteArrayOutputStream filtered = new ByteArrayOutputStream();
        new FilteredHollowBlobWriter(excludeChild)
                .filterSnapshot(new ByteArrayInputStream(snapshot.toByteArray()), filtered);
        byte[] blobMissingChild = filtered.toByteArray();

        // 3. Read that blob with a recursive type filter that walks Parent -> child -> (missing) Child.
        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        assertThatCode(() ->
                reader.readSnapshot(
                        HollowBlobInput.serial(blobMissingChild),
                        newTypeFilter().excludeAll().includeRecursive("Parent").build()))
                .doesNotThrowAnyException();

        // 4. Parent is loaded and readable; the missing Child type is simply not in the read state.
        assertThat(readEngine.getSchema("Child")).isNull();
        assertThat(readEngine.getSchema("Parent")).isNotNull();
        assertThat(readEngine.getTypeState("Parent")).isNotNull();
        assertThat(readEngine.getTypeState("Parent").getPopulatedOrdinals().cardinality()).isEqualTo(2);

        int firstOrdinal = readEngine.getTypeState("Parent").getPopulatedOrdinals().nextSetBit(0);
        assertThat(new GenericHollowObject(readEngine, "Parent", firstOrdinal).getInt("id")).isIn(1, 2);
    }

    /**
     * When a map's value type is absent, the resolved filter keeps the map field/type included.
     * This verifies that doing so is safe "when actually loading the data".
     *
     * <p>A value type with zero records (the RawHollow trigger) has no ordinals, so no map entry can
     * reference one -- every surviving map instance is empty. Loading only needs the key type
     * ({@code buildKeyDeriver}); the value type is dereferenced lazily, per entry, so iterating an
     * empty map never materializes a (missing) value.
     */
    @Test
    public void readsMapWithRecursiveFilterWhenValueTypeSchemaIsAbsent() throws IOException {
        // Holder with an empty map: the value type MapValue has zero records, mirroring RawHollow.
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        mapper.add(new Holder(new HashMap<>()));

        ByteArrayOutputStream snapshot = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(snapshot);

        // Drop the map's value type from the header, keeping Holder and the map type itself.
        HollowFilterConfig excludeValue = new HollowFilterConfig(true); // exclude filter
        excludeValue.addType("MapValue");
        ByteArrayOutputStream filtered = new ByteArrayOutputStream();
        new FilteredHollowBlobWriter(excludeValue)
                .filterSnapshot(new ByteArrayInputStream(snapshot.toByteArray()), filtered);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);

        // Loading the data with a recursive filter that walks Holder -> m -> map(value = missing).
        assertThatCode(() ->
                reader.readSnapshot(
                        HollowBlobInput.serial(filtered.toByteArray()),
                        newTypeFilter().excludeAll().includeRecursive("Holder").build()))
                .doesNotThrowAnyException();

        assertThat(readEngine.getSchema("MapValue")).isNull();
        assertThat(readEngine.getTypeState("Holder")).isNotNull();

        // Reading the map field and iterating it is safe: the (empty) map materializes no value.
        int holderOrdinal = readEngine.getTypeState("Holder").getPopulatedOrdinals().nextSetBit(0);
        GenericHollowObject holder = new GenericHollowObject(readEngine, "Holder", holderOrdinal);
        assertThatCode(() -> {
            GenericHollowMap map = holder.getMap("m");
            int entryCount = 0;
            for (Map.Entry<HollowRecord, HollowRecord> entry : map.<HollowRecord, HollowRecord>entries()) {
                entry.getKey();
                entry.getValue();
                entryCount++;
            }
            assertThat(entryCount).isZero();
        }).doesNotThrowAnyException();
    }

    /**
     * Lifecycle question: if a referenced type that was absent (zero records) at the snapshot the
     * consumer loaded later gains records, does the consumer pick it up?
     *
     * <p>Two mechanisms, verified here:
     * <ul>
     *   <li><b>Delta</b>: {@code readTypeStateDelta} discards a delta for a type that has no read
     *       state ({@code getTypeState == null}); it never creates a new type state. So a long-running
     *       consumer that applies only deltas will NOT pick up the type until it re-snapshots.</li>
     *   <li><b>Fresh snapshot</b>: once the type has records, its schema is back in the header, the
     *       recursive filter re-resolves to include it, and it loads correctly.</li>
     * </ul>
     * This is pre-existing Hollow behavior for any type absent at snapshot time (filtered out or
     * omitted), not a consequence of the null-schema fix.
     */
    @Test
    public void referencedTypeThatGainsRecordsLoadsOnFreshSnapshotButIsDiscardedOnDelta() throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        HollowObjectMapper mapper = new HollowObjectMapper(writeEngine);
        HollowBlobWriter writer = new HollowBlobWriter(writeEngine);

        // Cycle 1: Parent present, no Child -> Child has zero records.
        mapper.add(new Parent(1, null));
        ByteArrayOutputStream snapshot1 = new ByteArrayOutputStream();
        writer.writeSnapshot(snapshot1);

        // Consumer loads a cycle-1 blob with Child dropped from the header (simulating RawHollow
        // omitting the zero-record type), using a recursive filter.
        HollowFilterConfig excludeChild = new HollowFilterConfig(true);
        excludeChild.addType("Child");
        ByteArrayOutputStream filteredSnap1 = new ByteArrayOutputStream();
        new FilteredHollowBlobWriter(excludeChild)
                .filterSnapshot(new ByteArrayInputStream(snapshot1.toByteArray()), filteredSnap1);

        HollowReadStateEngine readEngine = new HollowReadStateEngine();
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        reader.readSnapshot(HollowBlobInput.serial(filteredSnap1.toByteArray()),
                newTypeFilter().excludeAll().includeRecursive("Parent").build());
        assertThat(readEngine.getSchema("Child")).isNull();

        // Cycle 2: Child gains a record. Producer emits an (unfiltered) delta carrying Child.
        writeEngine.prepareForNextCycle();
        mapper.add(new Parent(1, null));
        mapper.add(new Parent(2, new Child(99)));
        ByteArrayOutputStream delta = new ByteArrayOutputStream();
        writer.writeDelta(delta);
        ByteArrayOutputStream snapshot2 = new ByteArrayOutputStream();
        writer.writeSnapshot(snapshot2);

        // Applying the delta: Parent's new record applies, but Child's delta is DISCARDED because the
        // consumer has no Child read state. Child remains absent.
        reader.applyDelta(HollowBlobInput.serial(delta.toByteArray()));
        assertThat(readEngine.getSchema("Child")).isNull();
        assertThat(readEngine.getTypeState("Parent").getPopulatedOrdinals().cardinality()).isEqualTo(2);

        // Re-initializing from the fresh cycle-2 snapshot (where Child now has records) loads Child.
        HollowReadStateEngine freshEngine = new HollowReadStateEngine();
        new HollowBlobReader(freshEngine).readSnapshot(HollowBlobInput.serial(snapshot2.toByteArray()),
                newTypeFilter().excludeAll().includeRecursive("Parent").build());
        assertThat(freshEngine.getSchema("Child")).isNotNull();
        assertThat(freshEngine.getTypeState("Child").getPopulatedOrdinals().cardinality()).isEqualTo(1);
    }

    @SuppressWarnings("unused")
    private static class Holder {
        Map<Integer, MapValue> m;

        Holder(Map<Integer, MapValue> m) {
            this.m = m;
        }
    }

    @SuppressWarnings("unused")
    private static class MapValue {
        int v;

        MapValue(int v) {
            this.v = v;
        }
    }

    @SuppressWarnings("unused")
    private static class Parent {
        int id;
        Child child;

        Parent(int id, Child child) {
            this.id = id;
            this.child = child;
        }
    }

    @SuppressWarnings("unused")
    private static class Child {
        int value;

        Child(int value) {
            this.value = value;
        }
    }
}
