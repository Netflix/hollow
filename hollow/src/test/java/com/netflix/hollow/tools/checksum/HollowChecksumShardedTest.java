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
package com.netflix.hollow.tools.checksum;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.generic.GenericHollowObject;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.HollowBlobInput;
import com.netflix.hollow.core.read.engine.HollowBlobReader;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowListSchema;
import com.netflix.hollow.core.schema.HollowMapSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSetSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
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
import com.netflix.hollow.test.InMemoryBlobStore;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;

/**
 * Verifies the per-(type, shard) parallel checksum (design B1). The checksum for a multi-shard type
 * is the deterministic combination of independent per-shard partial checksums, combined in ascending
 * shard order, then combined across types in sorted type-name order.
 */
public class HollowChecksumShardedTest {

    /**
     * The per-shard parallel checksum is opt-in (via {@code HollowProducer.Builder.withParallelPerShardChecksum}).
     * The no-arg / default path must be computed exactly as before: one partial per type (the serial per-type fold),
     * combined in sorted type-name order. This preserves the historical checksum value for callers that don't opt in.
     */
    @Test
    public void defaultChecksumUsesPerTypeComputation() throws IOException {
        HollowReadStateEngine readEngine = buildMultiShardObjectEngine("TypeA", 1000);
        HollowTypeReadState typeState = readEngine.getTypeState("TypeA");
        Assert.assertTrue("test requires a multi-shard type", typeState.numShards() > 1);

        HollowChecksum expected = new HollowChecksum();
        expected.applyType(new HollowChecksum.TypeChecksum("TypeA", typeState.getChecksum(typeState.getSchema())));

        HollowChecksum actual = HollowChecksum.forStateEngineWithCommonSchemas(readEngine, readEngine);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void forStateEngineCombinesPerShardPartialsInShardOrder() throws IOException {
        HollowReadStateEngine readEngine = buildMultiShardObjectEngine("TypeA", 1000);
        HollowTypeReadState typeState = readEngine.getTypeState("TypeA");
        Assert.assertTrue("test requires a multi-shard type", typeState.numShards() > 1);

        // Expected: combine each shard's independent partial checksum in ascending shard order into the
        // type checksum, then fold the (single) type checksum into the total in the usual way.
        HollowChecksum expectedTypeChecksum = new HollowChecksum();
        for (int shard = 0; shard < typeState.numShards(); shard++) {
            expectedTypeChecksum.applyInt(typeState.getShardChecksum(typeState.getSchema(), shard).intValue());
        }
        HollowChecksum expected = new HollowChecksum();
        expected.applyType(new HollowChecksum.TypeChecksum("TypeA", expectedTypeChecksum));

        HollowChecksum actual = HollowChecksum.forStateEngineWithCommonSchemas(readEngine, readEngine, true);

        Assert.assertEquals(expected, actual);
    }

    /**
     * Detection strength (design §4.3): the per-shard combine must remain sensitive to every record, so mutating a
     * single field of a single record changes the overall checksum. Guards against a combine that could cancel.
     */
    @Test
    public void detectsSingleRecordMutationInMultiShardObject() throws IOException {
        HollowChecksum baseline = HollowChecksum.forStateEngine(buildMultiShardObjectEngine("TypeA", 1000, -1), true);
        HollowChecksum mutated = HollowChecksum.forStateEngine(buildMultiShardObjectEngine("TypeA", 1000, 500), true);

        Assert.assertNotEquals(baseline, mutated);
    }

    /**
     * The per-shard fan-out must work for every type kind, not just OBJECT. Builds multi-shard LIST, SET and MAP
     * types and asserts the parallel checksum is equality-valid (identical data agrees) and detection-sensitive
     * (a change to any one type kind is detected). Exercises the per-shard checksum of each collection read state.
     */
    @Test
    public void parallelChecksumIsValidForListSetAndMapShards() throws IOException {
        HollowReadStateEngine baseline = buildMultiShardCollectionsEngine(null);
        Assert.assertTrue("LIST must be multi-shard", baseline.getTypeState("TestList").numShards() > 1);
        Assert.assertTrue("SET must be multi-shard", baseline.getTypeState("TestSet").numShards() > 1);
        Assert.assertTrue("MAP must be multi-shard", baseline.getTypeState("TestMap").numShards() > 1);

        HollowChecksum baselineChecksum = HollowChecksum.forStateEngine(baseline, true);

        // equality-validity: rebuilding the identical dataset yields the identical checksum
        Assert.assertEquals(baselineChecksum, HollowChecksum.forStateEngine(buildMultiShardCollectionsEngine(null), true));

        // detection: a change confined to any single type kind changes the overall checksum
        Assert.assertNotEquals(baselineChecksum, HollowChecksum.forStateEngine(buildMultiShardCollectionsEngine("TestList"), true));
        Assert.assertNotEquals(baselineChecksum, HollowChecksum.forStateEngine(buildMultiShardCollectionsEngine("TestSet"), true));
        Assert.assertNotEquals(baselineChecksum, HollowChecksum.forStateEngine(buildMultiShardCollectionsEngine("TestMap"), true));
    }

    /**
     * Validates the reshard open item (design §4.4/§9 #2). Reproduces {@code checkIntegrity}'s forward comparison
     * across a re-sharding cycle: a {@code current} engine advanced through a delta that changes numShards, and a
     * freshly-read {@code pending} snapshot of the same version. Because Hollow reshards the delta target to the
     * delta's numShards before applying it, the two engines end up structurally aligned (same numShards, aligned
     * ordinals) and their parallel checksums must agree — even though numShards changed.
     */
    @Test
    public void parallelChecksumsAgreeAcrossReshardingDelta() throws IOException {
        HollowObjectSchema schema = new HollowObjectSchema("TypeA", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("data", FieldType.STRING);

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.setTargetMaxTypeShardSize(4096);
        writeEngine.allowTypeResharding(true);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(schema));

        // Cycle 1: small dataset -> a baseline shard count. Snapshot it into `current`.
        addObjectRecords(writeEngine, schema, 0, 300);
        HollowReadStateEngine current = new HollowReadStateEngine();
        readSnapshotInto(current, writeSnapshot(writeEngine));
        writeEngine.prepareForNextCycle();
        int shardsBefore = current.getTypeState("TypeA").numShards();

        // Cycle 2: much larger dataset (superset) -> crosses a shard-doubling boundary, forcing a reshard.
        addObjectRecords(writeEngine, schema, 0, 6000);
        // Produce BOTH the fresh snapshot (pending) and the delta (advances current) from the SAME cycle,
        // exactly as the producer does when it runs its integrity check.
        byte[] snapshotBlob = writeSnapshot(writeEngine);
        byte[] deltaBlob = writeDelta(writeEngine);
        writeEngine.prepareForNextCycle();

        HollowReadStateEngine pending = new HollowReadStateEngine();
        readSnapshotInto(pending, snapshotBlob);
        applyDeltaTo(current, deltaBlob); // reshards `current` up to the delta's (== pending's) numShards

        int shardsAfter = current.getTypeState("TypeA").numShards();
        Assert.assertNotEquals("expected the delta to reshard the type", shardsBefore, shardsAfter);
        Assert.assertEquals("current must be resharded to match pending",
                pending.getTypeState("TypeA").numShards(), shardsAfter);

        // This is checkIntegrity's forward/reverse comparison: same logical state, compared both directions.
        HollowChecksum currentChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending, true);
        HollowChecksum pendingChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current, true);
        Assert.assertEquals(pendingChecksum, currentChecksum);
    }

    /**
     * The {@code parallelPerShard} argument selects the algorithm explicitly, and the no-arg method defaults to the
     * legacy per-type computation. {@code checkIntegrity} passes the same producer-configured value to all four of
     * its checksums, so they stay mutually consistent.
     */
    @Test
    public void overloadSelectsPerShardOrPerType() throws IOException {
        HollowReadStateEngine engine = buildMultiShardObjectEngine("TypeA", 1000);
        Assert.assertTrue(engine.getTypeState("TypeA").numShards() > 1);

        HollowChecksum perShard = HollowChecksum.forStateEngineWithCommonSchemas(engine, engine, true);
        HollowChecksum perType = HollowChecksum.forStateEngineWithCommonSchemas(engine, engine, false);
        Assert.assertNotEquals("per-shard and per-type differ for a multi-shard type", perShard, perType);

        // the no-arg method defaults to the legacy per-type computation
        Assert.assertEquals(perType, HollowChecksum.forStateEngineWithCommonSchemas(engine, engine));
    }

    @Test
    public void getShardChecksumRejectsOutOfRangeShard() throws IOException {
        HollowTypeReadState typeState = buildMultiShardObjectEngine("TypeA", 1000).getTypeState("TypeA");
        int numShards = typeState.numShards();
        Assert.assertTrue(numShards > 1);
        assertThrowsIllegalArgument(() -> typeState.getShardChecksum(typeState.getSchema(), numShards));
        assertThrowsIllegalArgument(() -> typeState.getShardChecksum(typeState.getSchema(), -1));
    }

    /**
     * C1: ordinal holes (records deleted across a delta leave unpopulated ordinals within {@code [0, maxOrdinal]}).
     * The per-shard checksum must remain equality-valid and detection-sensitive when shards contain holes.
     */
    @Test
    public void parallelChecksumHandlesOrdinalHolesFromDeletions() throws IOException {
        HollowReadStateEngine a = buildHolyMultiShardObjectEngine(-1);
        HollowReadStateEngine b = buildHolyMultiShardObjectEngine(-1);
        BitSet populated = a.getTypeState("TypeA").getPopulatedOrdinals();
        Assert.assertTrue("type must be multi-shard", a.getTypeState("TypeA").numShards() > 1);
        Assert.assertTrue("state must contain ordinal holes", populated.cardinality() < populated.length());

        // equality-validity across identical hole-y states
        Assert.assertEquals(HollowChecksum.forStateEngine(a, true), HollowChecksum.forStateEngine(b, true));
        // detection still fires when a surviving record changes
        HollowReadStateEngine mutated = buildHolyMultiShardObjectEngine(500);
        Assert.assertNotEquals(HollowChecksum.forStateEngine(a, true), HollowChecksum.forStateEngine(mutated, true));
    }

    /**
     * C2: full producer/consumer pipeline with the parallel checksum on, across a reshard. The producer runs its
     * integrity check on every delta cycle (resharding requires it), and that check computes the parallel checksum
     * over both the forward delta (which reshards {@code current} UP to N_new) and the reverse delta (which reshards
     * {@code pending} back DOWN to N_old) — so a single reshard cycle exercises the parallel checksum in both shard
     * directions. A broken parallel checksum would throw {@code ChecksumValidationException} and stall version
     * progression. The consumer must then read identical data following the delta chain across the reshard.
     */
    @Test
    public void producerConsumerEndToEndWithParallelChecksumAcrossReshard() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTypeResharding(true)
                .withParallelPerShardChecksum(true)
                .withTargetMaxTypeShardSize(2048)
                .build();
        producer.initializeDataModel(E2EPojo.class);

        long v1 = producer.runCycle(ws -> addE2ERecords(ws, 100));
        int shards1 = producer.getWriteEngine().getTypeState("E2EPojo").getNumShards();
        long v2 = producer.runCycle(ws -> addE2ERecords(ws, 8000)); // grow -> reshard; integrity check runs here
        int shards2 = producer.getWriteEngine().getTypeState("E2EPojo").getNumShards();
        long v3 = producer.runCycle(ws -> addE2ERecords(ws, 8000)); // steady multi-shard delta; integrity runs again

        Assert.assertTrue("versions must advance (integrity passed each cycle)", v1 < v2 && v2 < v3);
        Assert.assertTrue("expected a reshard (" + shards1 + "->" + shards2 + ")", shards2 > shards1);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v1);
        assertConsumerHasIds(consumer, 100);
        consumer.triggerRefreshTo(v2); // consumer applies a reshard delta
        assertConsumerHasIds(consumer, 8000);
        consumer.triggerRefreshTo(v3);
        assertConsumerHasIds(consumer, 8000);
    }

    /**
     * C3: the reverse leg of {@code checkIntegrity} across a reshard-DOWN. Mirrors the producer applying the reverse
     * delta to {@code pending}, which reshards it from N_new back to N_old; the reverse checksum must equal the
     * checksum captured on {@code current} at N_old before any delta.
     */
    @Test
    public void parallelChecksumsAgreeAcrossReverseReshardingDelta() throws IOException {
        HollowObjectSchema schema = objectSchemaTypeA();

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.setTargetMaxTypeShardSize(4096);
        writeEngine.allowTypeResharding(true);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(schema));

        // Cycle 1: small -> N_old. `current` = snapshot.
        addObjectRecords(writeEngine, schema, 0, 300);
        HollowReadStateEngine current = new HollowReadStateEngine();
        readSnapshotInto(current, writeSnapshot(writeEngine));
        writeEngine.prepareForNextCycle();
        int nOld = current.getTypeState("TypeA").numShards();

        // Cycle 2: grow -> N_new. Produce the snapshot (pending) and the reverse delta from the SAME cycle.
        addObjectRecords(writeEngine, schema, 0, 6000);
        byte[] snapshotBlob = writeSnapshot(writeEngine);
        byte[] reverseDeltaBlob = writeReverseDelta(writeEngine);
        writeEngine.prepareForNextCycle();

        HollowReadStateEngine pending = new HollowReadStateEngine();
        readSnapshotInto(pending, snapshotBlob);
        int nNew = pending.getTypeState("TypeA").numShards();
        Assert.assertNotEquals("expected the forward delta to reshard up", nOld, nNew);

        HollowChecksum currentChecksum = HollowChecksum.forStateEngineWithCommonSchemas(current, pending, true);
        applyDeltaTo(pending, reverseDeltaBlob); // reshards pending N_new -> N_old, back to `current`'s state
        Assert.assertEquals("reverse delta must reshard pending back down to N_old",
                nOld, pending.getTypeState("TypeA").numShards());
        HollowChecksum reverseChecksum = HollowChecksum.forStateEngineWithCommonSchemas(pending, current, true);

        Assert.assertEquals(currentChecksum, reverseChecksum);
    }

    private HollowReadStateEngine buildMultiShardObjectEngine(String typeName, int numRecords) throws IOException {
        return buildMultiShardObjectEngine(typeName, numRecords, -1);
    }

    private HollowReadStateEngine buildMultiShardObjectEngine(String typeName, int numRecords, int recordToMutate) throws IOException {
        HollowObjectSchema schema = new HollowObjectSchema(typeName, 2);
        schema.addField("id", FieldType.INT);
        schema.addField("data", FieldType.STRING);

        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.setTargetMaxTypeShardSize(4096);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(schema));

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < numRecords; i++) {
            rec.reset();
            rec.setInt("id", i);
            rec.setString("data", i == recordToMutate ? "value-" + i + "-mutated" : "value-" + i);
            writeEngine.add(typeName, rec);
        }

        return StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    /**
     * @param extraRecordInto if non-null, one extra distinct record is added to the named type so its data (and
     *                        hence its checksum) differs from the un-mutated build.
     */
    private HollowReadStateEngine buildMultiShardCollectionsEngine(String extraRecordInto) throws IOException {
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.setTargetMaxTypeShardSize(4096);
        writeEngine.addTypeState(new HollowListTypeWriteState(new HollowListSchema("TestList", "E")));
        writeEngine.addTypeState(new HollowSetTypeWriteState(new HollowSetSchema("TestSet", "E")));
        writeEngine.addTypeState(new HollowMapTypeWriteState(new HollowMapSchema("TestMap", "K", "V")));

        HollowListWriteRecord list = new HollowListWriteRecord();
        HollowSetWriteRecord set = new HollowSetWriteRecord();
        HollowMapWriteRecord map = new HollowMapWriteRecord();
        for (int i = 0; i < 2000; i++) {
            list.reset();
            list.addElement(i);
            list.addElement(i + 1);
            list.addElement(i + 2);
            writeEngine.add("TestList", list);

            set.reset();
            set.addElement(i);
            set.addElement(i + 1);
            set.addElement(i + 2);
            writeEngine.add("TestSet", set);

            map.reset();
            map.addEntry(i, i + 1);
            map.addEntry(i + 2, i + 3);
            map.addEntry(i + 4, i + 5);
            writeEngine.add("TestMap", map);
        }

        if ("TestList".equals(extraRecordInto)) {
            list.reset();
            list.addElement(999999);
            writeEngine.add("TestList", list);
        } else if ("TestSet".equals(extraRecordInto)) {
            set.reset();
            set.addElement(999999);
            writeEngine.add("TestSet", set);
        } else if ("TestMap".equals(extraRecordInto)) {
            map.reset();
            map.addEntry(999999, 999998);
            writeEngine.add("TestMap", map);
        }

        return StateEngineRoundTripper.roundTripSnapshot(writeEngine);
    }

    private void addObjectRecords(HollowWriteStateEngine writeEngine, HollowObjectSchema schema, int from, int to) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = from; i < to; i++) {
            rec.reset();
            rec.setInt("id", i);
            rec.setString("data", "value-" + i);
            writeEngine.add(schema.getName(), rec);
        }
    }

    private byte[] writeSnapshot(HollowWriteStateEngine writeEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeSnapshot(baos);
        return baos.toByteArray();
    }

    private byte[] writeDelta(HollowWriteStateEngine writeEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeDelta(baos);
        return baos.toByteArray();
    }

    private void readSnapshotInto(HollowReadStateEngine readEngine, byte[] blob) throws IOException {
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        try (HollowBlobInput in = HollowBlobInput.serial(blob)) {
            reader.readSnapshot(in);
        }
    }

    private void applyDeltaTo(HollowReadStateEngine readEngine, byte[] blob) throws IOException {
        HollowBlobReader reader = new HollowBlobReader(readEngine);
        try (HollowBlobInput in = HollowBlobInput.serial(blob)) {
            reader.applyDelta(in);
        }
    }

    private byte[] writeReverseDelta(HollowWriteStateEngine writeEngine) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngine).writeReverseDelta(baos);
        return baos.toByteArray();
    }

    private HollowObjectSchema objectSchemaTypeA() {
        HollowObjectSchema schema = new HollowObjectSchema("TypeA", 2);
        schema.addField("id", FieldType.INT);
        schema.addField("data", FieldType.STRING);
        return schema;
    }

    /**
     * Builds a multi-shard OBJECT read engine that contains ordinal holes: cycle 1 populates ids 0..999, cycle 2
     * (a delta) keeps only the even ids, so the odd ids' ordinals become unpopulated holes within the ordinal space.
     *
     * @param recordToMutate an even id whose {@code data} field is changed (or -1 for none), for detection tests
     */
    private HollowReadStateEngine buildHolyMultiShardObjectEngine(int recordToMutate) throws IOException {
        HollowObjectSchema schema = objectSchemaTypeA();
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        writeEngine.setTargetMaxTypeShardSize(4096);
        writeEngine.addTypeState(new HollowObjectTypeWriteState(schema));

        addObjectRecords(writeEngine, schema, 0, 1000);
        HollowReadStateEngine engine = new HollowReadStateEngine();
        readSnapshotInto(engine, writeSnapshot(writeEngine));
        writeEngine.prepareForNextCycle();

        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(schema);
        for (int i = 0; i < 1000; i += 2) {
            rec.reset();
            rec.setInt("id", i);
            rec.setString("data", i == recordToMutate ? "value-" + i + "-mutated" : "value-" + i);
            writeEngine.add("TypeA", rec);
        }
        applyDeltaTo(engine, writeDelta(writeEngine));
        return engine;
    }

    private void addE2ERecords(HollowProducer.WriteState ws, int count) {
        for (int i = 0; i < count; i++) {
            ws.add(new E2EPojo(i, "value-" + i));
        }
    }

    private void assertConsumerHasIds(HollowConsumer consumer, int expectedCount) {
        HollowTypeReadState typeState = consumer.getStateEngine().getTypeState("E2EPojo");
        BitSet populated = typeState.getPopulatedOrdinals();
        Set<Integer> ids = new HashSet<>();
        int ordinal = populated.nextSetBit(0);
        while (ordinal != -1) {
            ids.add(new GenericHollowObject(consumer.getStateEngine(), "E2EPojo", ordinal).getInt("id"));
            ordinal = populated.nextSetBit(ordinal + 1);
        }
        Set<Integer> expected = new TreeSet<>();
        for (int i = 0; i < expectedCount; i++) {
            expected.add(i);
        }
        Assert.assertEquals(new TreeSet<>(expected), new TreeSet<>(ids));
    }

    private void assertThrowsIllegalArgument(Runnable runnable) {
        try {
            runnable.run();
            Assert.fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            // expected
        }
    }

    @SuppressWarnings("unused")
    private static class E2EPojo {
        int id;
        String data;

        E2EPojo(int id, String data) {
            this.id = id;
            this.data = data;
        }
    }
}
