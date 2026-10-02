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
 */
package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.core.memory.FixedLengthData;
import com.netflix.hollow.core.memory.FixedLengthDataFactory;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.memory.pool.RecyclingRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;

public class ContiguousFixedLengthDataTest {

    @Test
    public void matchesSegmentedReadsAndWrites() {
        Random random = new Random(42);

        for (int bits = 1; bits <= 64; bits++) {
            int count = 257;
            long numBits = (long) count * bits;
            long mask = bits == 64 ? -1 : (1L << bits) - 1;
            FixedLengthData contiguous = new ContiguousFixedLengthData(numBits);
            FixedLengthData segmented =
                    new FixedLengthElementArray(WastefulRecycler.SMALL_ARRAY_RECYCLER, numBits);

            for (int i = 0; i < count; i++) {
                long value = random.nextLong() & mask;
                long index = (long) i * bits;
                contiguous.setElementValue(index, bits, value);
                segmented.setElementValue(index, bits, value);
            }

            for (int i = 0; i < count; i++) {
                long index = (long) i * bits;
                assertEquals(
                        segmented.getLargeElementValue(index, bits),
                        contiguous.getLargeElementValue(index, bits));
                if (bits <= 58) {
                    assertEquals(
                            segmented.getElementValue(index, bits),
                            contiguous.getElementValue(index, bits));
                }
            }
        }
    }

    @Test
    public void supportsCopyClearAndIncrement() {
        int bits = 31;
        int count = 1000;
        FixedLengthData source = new ContiguousFixedLengthData((long) bits * count);
        FixedLengthData copy = new ContiguousFixedLengthData((long) bits * count + 17);

        for (int i = 0; i < count; i++) {
            source.setElementValue((long) i * bits, bits, i * 13L);
        }
        copy.copyBits(source, 5, 17, (long) bits * count - 5);

        for (long offset = 0; offset < (long) bits * count - 5; offset += 53) {
            int width = (int) Math.min(53, (long) bits * count - 5 - offset);
            assertEquals(
                    source.getLargeElementValue(5 + offset, width),
                    copy.getLargeElementValue(17 + offset, width));
        }

        source.clearElementValue(10L * bits, bits);
        assertEquals(0, source.getElementValue(10L * bits, bits));

        source.incrementMany(0, 7, bits, count);
        for (int i = 0; i < count; i++) {
            long expected = i == 10 ? 7 : i * 13L + 7;
            assertEquals(expected, source.getElementValue((long) i * bits, bits));
        }
    }

    @Test
    public void factoryUsesContiguousStorageOnlyWithoutRecycling() throws IOException {
        FixedLengthData allocated = FixedLengthDataFactory.get(
                1024, MemoryMode.ON_HEAP, WastefulRecycler.DEFAULT_INSTANCE);
        assertTrue(allocated instanceof ContiguousFixedLengthData);

        FixedLengthData recycled = FixedLengthDataFactory.get(
                1024, MemoryMode.ON_HEAP, new RecyclingRecycler());
        assertTrue(recycled instanceof FixedLengthElementArray);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bytes);
        VarInt.writeVLong(out, 2);
        out.writeLong(0x0123456789ABCDEFL);
        out.writeLong(0x0FEDCBA987654321L);

        FixedLengthData loaded = FixedLengthDataFactory.get(
                HollowBlobInput.serial(bytes.toByteArray()),
                MemoryMode.ON_HEAP,
                WastefulRecycler.DEFAULT_INSTANCE);
        assertTrue(loaded instanceof ContiguousFixedLengthData);
        assertEquals(0x0123456789ABCDEFL, loaded.getLargeElementValue(0, 64));
        assertEquals(0x0FEDCBA987654321L, loaded.getLargeElementValue(64, 64));

        FixedLengthDataFactory.destroy(loaded, WastefulRecycler.DEFAULT_INSTANCE);
    }
}
