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
public class ByteDataArray implements ByteDataWrapper {

    private final SegmentedByteArray buf;
    private long position;

    public ByteDataArray() {
        this(WastefulRecycler.DEFAULT_INSTANCE);
    }

    public ByteDataArray(ArraySegmentRecycler memoryRecycler) {
        buf = new SegmentedByteArray(memoryRecycler);
    }

    @Override
    public void write(byte b) {
        buf.set(position++, b);
    }

    @Override
    public void reset() {
        position = 0;
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
    }

    @Override
    public long length() {
        return position;
    }

    @Override
    public void copyTo(ByteDataWrapper other) {
        other.getUnderlyingVariableLengthData().copy(buf, 0, other.getPosition(), position);
        other.setPosition(other.getPosition() + position);
    }

    @Override
    public void copyFrom(ByteData data, long startPosition, int length) {
        buf.copy(data, startPosition, position, length);
        position += length;
    }

    @Override
    public void copyFrom(VariableLengthData data, long startPosition, int length) {
        buf.copy(data, startPosition, position, length);
        position += length;
    }

    @Override
    public byte get(long index) {
        return buf.get(index);
    }

    @Override
    public VariableLengthData getUnderlyingVariableLengthData() {
        return buf;
    }

    public SegmentedByteArray getUnderlyingArray() {    // needed for backwards compatibilty
        return buf;
    }
}
