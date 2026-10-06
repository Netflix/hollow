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

import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Random;
import org.junit.Test;

public class FixedLengthElementArrayLargeReadTest {

    @Test
    public void matchesBitOracleAcrossSegmentBoundariesAndUpdates() {
        Random random = new Random(42);
        for (int log2 : new int[] {1, 2, 5, 8}) {
            long[] words = new long[3 * (1 << log2) + 7];
            FixedLengthElementArray data = new FixedLengthElementArray(
                    new WastefulRecycler(5, log2), words.length * 64L);
            for (int phase = 0; phase < 2; phase++) {
                for (int i = 0; i < words.length; i++) {
                    words[i] = random.nextLong();
                    data.set(i, words[i]);
                }
                assertLargeReadsMatch(data, words);
            }
        }
    }

    @Test
    public void readsCopiedBitsAcrossSegmentBoundaries() {
        Random random = new Random(42);
        for (int log2 : new int[] {1, 2, 5, 8}) {
            long[] sourceWords = new long[3 * (1 << log2) + 7];
            WastefulRecycler recycler = new WastefulRecycler(5, log2);
            FixedLengthElementArray source = new FixedLengthElementArray(recycler, sourceWords.length * 64L);
            FixedLengthElementArray copy = new FixedLengthElementArray(recycler, sourceWords.length * 64L);
            for (int i = 0; i < sourceWords.length; i++) {
                sourceWords[i] = random.nextLong();
                source.set(i, sourceWords[i]);
            }

            int start = (1 << log2) * 64 - 17;
            int length = sourceWords.length * 64 - start - 31;
            copy.copyBits(source, start, 11, length);
            long[] expected = new long[sourceWords.length];
            for (int i = 0; i < length; i++) {
                int from = start + i;
                if (((sourceWords[from >>> 6] >>> (from & 63)) & 1) != 0) {
                    int to = 11 + i;
                    expected[to >>> 6] |= 1L << (to & 63);
                }
            }
            assertLargeReadsMatch(copy, expected);
        }
    }

    @Test
    public void readsLoadedFenceposts() throws IOException {
        Random random = new Random(42);
        for (int log2 : new int[] {1, 2, 5, 8}) {
            long[] words = new long[3 * (1 << log2) + 7];
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataOutputStream out = new DataOutputStream(bytes)) {
                VarInt.writeVLong(out, words.length);
                for (int i = 0; i < words.length; i++) {
                    words[i] = random.nextLong();
                    out.writeLong(words[i]);
                }
            }
            try (HollowBlobInput in = HollowBlobInput.serial(bytes.toByteArray())) {
                FixedLengthElementArray loaded = FixedLengthElementArray.newFrom(in, new WastefulRecycler(5, log2));
                assertLargeReadsMatch(loaded, words);
            }
        }
    }

    @Test
    public void readsFencepostsAfterSetIncrementAndClear() {
        FixedLengthElementArray data = new FixedLengthElementArray(new WastefulRecycler(5, 2), 512);
        long[] words = new long[8];
        int boundary = 4 * 64;
        int index = boundary - 3;
        long value = 12345;
        data.setElementValue(index, 17, value);
        words[3] = value << 61;
        words[4] = value >>> 3;
        assertLargeReadsMatch(data, words);

        data.increment(index, 7);
        value += 7;
        words[3] = value << 61;
        words[4] = value >>> 3;
        assertLargeReadsMatch(data, words);

        data.clearElementValue(boundary + 4, 8);
        words[4] &= ~(0xFFL << 4);
        assertLargeReadsMatch(data, words);
    }

    @Test
    public void preservesOverriddenGet() {
        long transform = 0x9E3779B97F4A7C15L;
        FixedLengthElementArray data = new FixedLengthElementArray(new WastefulRecycler(5, 2), 512) {
            @Override
            public long get(long index) {
                return super.get(index) ^ transform;
            }
        };
        long[] words = new long[8];
        Random random = new Random(42);
        for (int i = 0; i < words.length; i++) {
            long raw = random.nextLong();
            data.set(i, raw);
            words[i] = raw ^ transform;
        }
        assertLargeReadsMatch(data, words);
    }

    private static void assertLargeReadsMatch(FixedLengthElementArray data, long[] words) {
        long totalBits = words.length * 64L;
        for (long index = 0; index < totalBits; index++) {
            for (int width = 1; width <= 64 && index + width <= totalBits; width++) {
                int word = (int) (index >>> 6);
                int offset = (int) (index & 63);
                long mask = width == 64 ? -1 : (1L << width) - 1;
                long expected = words[word] >>> offset;
                if (64 - offset < width) {
                    expected |= words[word + 1] << (64 - offset);
                }
                expected &= mask;
                assertEquals(expected, data.getLargeElementValue(index, width));
                assertEquals(expected, data.getLargeElementValue(index, width, mask));
                long partialMask = mask & 0x5555555555555555L;
                assertEquals(expected & partialMask, data.getLargeElementValue(index, width, partialMask));
            }
        }
    }
}
