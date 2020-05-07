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
package com.netflix.hollow.core.memory;

import com.netflix.hollow.core.memory.pool.ArraySegmentRecycler;
import com.netflix.hollow.core.memory.pool.WastefulRecycler;


/**
 * Writes data to a {@link SegmentedByteArray}, tracking the index to which it writes.
 *
 * @author dkoszewnik
 *
 */
public class ByteDataBuffer {

    private final SegmentedByteArray buf;
    private long position;

    public ByteDataBuffer() {
        this(WastefulRecycler.DEFAULT_INSTANCE);
    }

    public ByteDataBuffer(ArraySegmentRecycler memoryRecycler) {
        buf = new SegmentedByteArray(memoryRecycler);
    }

    public void write(byte b) {
        buf.set(position++, b);
    }

    public void reset() {
        position = 0;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public long length() {
        return position;
    }

    public void copyTo(ByteDataBuffer other) {
        other.buf.copy(buf, 0, other.position, position);
        other.position += position;
    }

    public void copyFrom(ByteData data, long startPosition, int length) {
        buf.copy(data, startPosition, position, length);
        position += length;
    }

    public void copyFrom(SegmentedByteArray data, long startPosition, int length) {
        buf.copy(data, startPosition, position, length);
        position += length;
    }

    public byte get(long index) {
        return buf.get(index);
    }

    public SegmentedByteArray getUnderlyingArray() {
        return buf;
    }
}
