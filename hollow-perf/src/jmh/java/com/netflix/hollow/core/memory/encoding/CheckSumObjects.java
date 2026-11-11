/*
 *  Copyright 2026 New Relic
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
package com.netflix.hollow.core.memory.encoding;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowObjectSchema.FieldType;
import com.netflix.hollow.core.schema.HollowSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class CheckSumObjects {

    static final String TYPE = "Entity";

    HollowTypeReadState typeReadState;
    HollowSchema schema;

    @Param("100000")
    private int n = 100000;

    @Param("8")
    private int shards = 8;

    // Remove every 20th record via a delta so the populated ordinals have holes
    @Param("false")
    private boolean remove = false;

    @Setup
    public void setUp() throws IOException {
        HollowObjectSchema s = new HollowObjectSchema(TYPE, 3);
        s.addField("id", FieldType.LONG);
        s.addField("count", FieldType.INT);
        s.addField("guid", FieldType.STRING);

        HollowWriteStateEngine w = new HollowWriteStateEngine();
        w.addTypeState(new HollowObjectTypeWriteState(s, shards));

        add(w, s, false);
        HollowReadStateEngine readState = StateEngineRoundTripper.roundTripSnapshot(w);

        if (remove) {
            w.prepareForNextCycle();
            add(w, s, true);
            StateEngineRoundTripper.roundTripDelta(w, readState);
        }

        typeReadState = readState.getTypeState(TYPE);
        schema = typeReadState.getSchema();
    }

    private void add(HollowWriteStateEngine w, HollowObjectSchema s, boolean skipEvery20th) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord(s);
        for (int i = 0; i < n; i++) {
            if (skipEvery20th && i % 20 == 0)
                continue;
            rec.reset();
            rec.setLong("id", i);
            rec.setInt("count", i & 0xFFFF);
            rec.setString("guid", new UUID(i, ~i).toString());
            w.add(TYPE, rec);
        }
    }

    @Benchmark
    public int checkSum() {
        return typeReadState.getChecksum(schema).intValue();
    }
}
