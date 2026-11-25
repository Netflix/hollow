/*
 * Copyright 2026 Netflix, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.core.read.engine.object;

import com.netflix.hollow.core.memory.HollowStringValue;
import com.netflix.hollow.core.memory.SegmentedByteArray;
import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.read.dataaccess.missing.HollowObjectMissingDataAccess;
import com.netflix.hollow.core.read.dataaccess.proxy.HollowObjectProxyDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.util.StateEngineRoundTripper;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import java.util.Arrays;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

public class HollowPreparedStringEqualityTest {
    @Test
    public void comparesEveryAsciiMismatchPositionAcrossSegments() {
        Random random = new Random(856);
        for(int start : new int[] { 0, 1, 31, 32, 33 }) {
            for(int length : new int[] { 0, 1, 7, 8, 15, 16, 31, 32, 33, 257 }) {
                SegmentedByteArray data = new SegmentedByteArray(new WastefulRecycler(5, 2));
                char[] chars = new char[length];
                for(int i = 0; i < length; i++) {
                    chars[i] = (char)random.nextInt(128);
                    data.set(start + i, (byte)chars[i]);
                }
                assertTrue(data.isVIntStringEqualTo(start, length, HollowStringValue.of(new String(chars))));
                assertFalse(data.isVIntStringEqualTo(start, length, HollowStringValue.of(new String(chars) + "x")));
                for(int i = 0; i < length; i++) {
                    chars[i] ^= 1;
                    assertFalse(data.isVIntStringEqualTo(start, length, HollowStringValue.of(new String(chars))));
                    chars[i] ^= 1;
                }
            }
        }
    }

    @Test
    public void comparesRealSnapshotWithBothRecyclerModesAndDeltas() throws Exception {
        for(boolean recycling : new boolean[] { false, true }) {
            HollowObjectSchema schema = new HollowObjectSchema("Config", 1);
            schema.addField("config", HollowObjectSchema.FieldType.STRING);
            HollowWriteStateEngine writer = new HollowWriteStateEngine();
            writer.addTypeState(new HollowObjectTypeWriteState(schema));
            ArraySegmentRecycler recycler = recycling ? new RecyclingRecycler(5, 2) : new WastefulRecycler(5, 2);
            HollowReadStateEngine reader = new HollowReadStateEngine(recycler);
            char[] large = new char[8192];
            Arrays.fill(large, 'a');
            String[] values = { "", "abc", new String(large), "\u0000\u007f", "\u0080\u65e5\uffff", "\ud83d\ude00", "\ud800", "\udc00", null };
            int[] ordinals = new int[values.length];
            for(int i = 0; i < values.length; i++) ordinals[i] = add(writer, schema, values[i]);
            StateEngineRoundTripper.roundTripSnapshot(writer, reader, null);
            HollowObjectTypeDataAccess access = (HollowObjectTypeDataAccess)reader.getTypeDataAccess("Config");
            HollowObjectProxyDataAccess proxy = new HollowObjectProxyDataAccess(null);
            proxy.setCurrentDataAccess(access);
            for(int i = 0; i < values.length; i++) {
                for(String candidate : values) {
                    if(candidate == null) continue;
                    assertEquals(candidate.equals(values[i]), access.isStringFieldEqualTo(ordinals[i], 0, HollowStringValue.of(candidate)));
                    assertEquals(candidate.equals(values[i]), proxy.isStringFieldEqualTo(ordinals[i], 0, HollowStringValue.of(candidate)));
                }
            }
            HollowStringValue old = HollowStringValue.of("abc");
            int changed = add(writer, schema, "updated");
            StateEngineRoundTripper.roundTripDelta(writer, reader);
            assertFalse(access.isStringFieldEqualTo(changed, 0, old));
            assertTrue(access.isStringFieldEqualTo(changed, 0, HollowStringValue.of("updated")));
        }
    }

    @Test
    public void defaultsToStringEqualityForOtherReaders() {
        HollowObjectTypeDataAccess access = new HollowObjectMissingDataAccess(null, "Config") {
            @Override
            public String readString(int ordinal, int fieldIndex) {
                return "\u65e5";
            }
        };
        assertTrue(access.isStringFieldEqualTo(4, 2, HollowStringValue.of("\u65e5")));
        assertFalse(access.isStringFieldEqualTo(4, 2, HollowStringValue.of("abc")));
    }

    @Test(expected = NullPointerException.class)
    public void rejectsNullPreparedValue() {
        HollowStringValue.of(null);
    }

    private static int add(HollowWriteStateEngine writer, HollowObjectSchema schema, String value) {
        HollowObjectWriteRecord record = new HollowObjectWriteRecord(schema);
        if(value != null) record.setString("config", value);
        return writer.add("Config", record);
    }
}
