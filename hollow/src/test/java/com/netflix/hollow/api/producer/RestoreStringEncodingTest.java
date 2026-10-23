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
package com.netflix.hollow.api.producer;

import static org.junit.Assert.assertEquals;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.InMemoryBlobStore;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * A restored producer must re-create records with exactly the serialized bytes they were published with. Otherwise
 * a record whose STRING field bytes are not in canonical form (e.g. {@code [0x80]}, which is what
 * {@link HollowObjectWriteRecord#setNull(String)} produces for a STRING field and which reads back as {@code ""}) is
 * re-encoded on restore, so the first cycle after restore neither reuses its ordinal nor produces a reverse delta
 * that passes the integrity check ({@code REVERSE_DELTA has invalid checksums}).
 */
public class RestoreStringEncodingTest {

    private static final HollowObjectSchema SCHEMA = new HollowObjectSchema("Record", 2);
    static {
        SCHEMA.addField("id", FieldType.INT);
        SCHEMA.addField("value", FieldType.STRING);
    }

    @Test
    public void firstCycleAfterRestorePassesIntegrityCheckAndKeepsOrdinals() {
        InMemoryBlobStore blobStore = new InMemoryBlobStore();

        HollowProducer producer1 = newProducer(blobStore);
        long v1 = producer1.runCycle(state -> populate(state.getStateEngine(), 2));
        Map<Integer, Integer> ordinalsV1 = ordinalsById(readState(blobStore, v1));

        HollowProducer producer2 = newProducer(blobStore);
        producer2.restore(v1, blobStore);
        // same records plus one new one, so that the cycle produces a delta and a reverse delta
        long v2 = producer2.runCycle(state -> populate(state.getStateEngine(), 3));

        Map<Integer, Integer> ordinalsV2 = ordinalsById(readState(blobStore, v2));
        assertEquals(ordinalsV1.get(0), ordinalsV2.get(0)); // setNull("value") record
        assertEquals(ordinalsV1.get(1), ordinalsV2.get(1)); // setString("value", "") record
        assertEquals(ordinalsV1.get(2), ordinalsV2.get(2)); // setString("value", "a") record
    }

    private static void populate(HollowWriteStateEngine engine, int numRecords) {
        for (int id = 0; id <= numRecords; id++) {
            HollowObjectWriteRecord rec = new HollowObjectWriteRecord(SCHEMA);
            rec.setInt("id", id);
            if (id == 0) {
                rec.setNull("value");
            } else if (id == 1) {
                rec.setString("value", "");
            } else {
                rec.setString("value", id == 2 ? "a" : "b" + id);
            }
            engine.add("Record", rec);
        }
    }

    private static HollowProducer newProducer(InMemoryBlobStore blobStore) {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(SCHEMA);
        return producer;
    }

    private static HollowReadStateEngine readState(InMemoryBlobStore blobStore, long version) {
        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);
        return consumer.getStateEngine();
    }

    private static Map<Integer, Integer> ordinalsById(HollowReadStateEngine readEngine) {
        HollowObjectTypeReadState typeState = (HollowObjectTypeReadState) readEngine.getTypeState("Record");
        Map<Integer, Integer> ordinals = new HashMap<>();
        BitSet populated = typeState.getPopulatedOrdinals();
        for (int ordinal = populated.nextSetBit(0); ordinal >= 0; ordinal = populated.nextSetBit(ordinal + 1)) {
            ordinals.put(typeState.readInt(ordinal, typeState.getSchema().getPosition("id")), ordinal);
        }
        return ordinals;
    }
}
