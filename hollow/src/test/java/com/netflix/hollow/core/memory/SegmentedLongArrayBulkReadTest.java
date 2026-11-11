/*
 * Copyright 2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.hollow.core.memory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;
import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Test;

public class SegmentedLongArrayBulkReadTest {
    @Test
    public void readFrom_preservesValuesAndFenceposts() throws Exception {
        ArraySegmentRecycler recycler = WastefulRecycler.DEFAULT_INSTANCE;
        int size = 1 << recycler.getLog2OfLongSegmentSize();
        for (int count : new int[] {0, 1, 2, size - 1, size, size + 1, 2 * size, 2 * size + 17, 1048576}) {
            for (int streamKind = 0; streamKind < 3; streamKind++) {
                byte[] bytes = new byte[count * Long.BYTES];
                new Random(42).nextBytes(bytes);
                SegmentedLongArray array = new SegmentedLongArray(recycler, count);
                InputStream source = new ByteArrayInputStream(bytes);
                if (streamKind == 1) {
                    source = new BufferedInputStream(source);
                } else if (streamKind == 2) {
                    source = new ByteArrayInputStream(bytes) {
                        @Override
                        public synchronized int read(byte[] b, int offset, int length) {
                            return super.read(b, offset, Math.min(length, 3));
                        }
                    };
                }
                try (HollowBlobInput input = HollowBlobInput.serial(source)) {
                    array.readFrom(input, recycler, count);
                    assertEquals(0, ((DataInputStream) input.getInput()).available());
                }
                ByteBuffer expected = ByteBuffer.wrap(bytes);
                for (int i = 0; i < count; i++) {
                    assertEquals(expected.getLong(i * Long.BYTES), array.get(i));
                }
                for (int segment = 0; segment < array.segments.length; segment++) {
                    long[] values = new long[size + 1];
                    int start = segment * size;
                    for (int i = 0; i < size && start + i < count; i++) {
                        values[i] = expected.getLong((start + i) * Long.BYTES);
                    }
                    if (start + size < count) {
                        values[size] = expected.getLong((start + size) * Long.BYTES);
                    }
                    assertArrayEquals(values, array.segments[segment]);
                }
            }
        }
    }

    @Test
    public void readFrom_rejectsTruncatedInput() throws IOException {
        ArraySegmentRecycler recycler = WastefulRecycler.DEFAULT_INSTANCE;
        SegmentedLongArray array = new SegmentedLongArray(recycler, 257);
        try (HollowBlobInput input = HollowBlobInput.serial(new byte[257 * Long.BYTES - 1])) {
            try {
                array.readFrom(input, recycler, 257);
                fail("Expected truncated input to fail");
            } catch (EOFException expected) {
                // DataInput's expected failure channel for incomplete input.
            }
        }
    }
}
