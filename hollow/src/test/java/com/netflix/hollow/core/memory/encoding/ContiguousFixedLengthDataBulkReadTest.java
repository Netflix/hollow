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
package com.netflix.hollow.core.memory.encoding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Random;
import org.junit.Test;

public class ContiguousFixedLengthDataBulkReadTest {
    @Test
    public void newFrom_preservesValuesAndInputPosition() throws Exception {
        for (int count : new int[] {0, 1, 2, 255, 256, 257, 512, 529, 1048576}) {
            byte[] bytes = new byte[count * Long.BYTES + 1];
            new Random(42).nextBytes(bytes);
            for (int streamKind = 0; streamKind < 3; streamKind++) {
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
                    ContiguousFixedLengthData data = ContiguousFixedLengthData.newFrom(input, count);
                    ByteBuffer expected = ByteBuffer.wrap(bytes);
                    for (int i = 0; i < count; i++) {
                        assertEquals(expected.getLong(i * Long.BYTES),
                                data.getLargeElementValue((long) i * 64, 64));
                    }
                    if (count > 0) {
                        assertEquals(expected.getLong((count - 1) * Long.BYTES) >>> 1,
                                data.getLargeElementValue((long) count * 64 - 63, 64));
                    }
                    assertEquals(bytes[count * Long.BYTES] & 255, input.read());
                    assertEquals(-1, source.read());
                }
            }
        }
    }

    @Test
    public void newFrom_rejectsTruncatedInput() throws Exception {
        for (int count : new int[] {1, 256, 257, 529}) {
            try (HollowBlobInput input = HollowBlobInput.serial(new byte[count * Long.BYTES - 1])) {
                try {
                    ContiguousFixedLengthData.newFrom(input, count);
                    fail("Expected truncated input to fail");
                } catch (EOFException expected) {
                    // DataInput's expected failure channel for incomplete input.
                }
            }
        }
    }
}
