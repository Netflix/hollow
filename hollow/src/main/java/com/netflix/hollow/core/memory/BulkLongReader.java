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

import com.netflix.hollow.core.read.HollowBlobInput;
import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import sun.misc.Unsafe;

/** Reads big-endian longs using a scratch buffer reused across destination arrays. */
public final class BulkLongReader {
    private static final Unsafe unsafe = HollowUnsafeHandle.getUnsafe();
    private final DataInput input;
    private final byte[] bytes;
    private final ByteBuffer buffer;

    public BulkLongReader(HollowBlobInput input, int bufferLongs) {
        if (bufferLongs <= 0) {
            throw new IllegalArgumentException("Buffer size must be positive: " + bufferLongs);
        }
        this.input = (DataInput) input.getInput();
        this.bytes = new byte[Math.multiplyExact(bufferLongs, Long.BYTES)];
        this.buffer = ByteBuffer.wrap(bytes);
    }

    public void readFully(long[] destination, int offset, int count) throws IOException {
        while (count > 0) {
            int chunk = Math.min(bytes.length / Long.BYTES, count);
            input.readFully(bytes, 0, chunk * Long.BYTES);
            for (int i = 0; i < chunk; i++) {
                unsafe.putLong(destination, (long) Unsafe.ARRAY_LONG_BASE_OFFSET
                        + (long) Long.BYTES * (offset + i), buffer.getLong(i * Long.BYTES));
            }
            offset += chunk;
            count -= chunk;
        }
    }
}
