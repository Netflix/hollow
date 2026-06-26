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
package com.netflix.hollow.core.memory.pool;

/**
 * A WastefulRecycler is an {@link ArraySegmentRecycler} which doesn't <i>really</i> pool arrays, it instead
 * just creates them on the demand. 
 */
public class WastefulRecycler implements ArraySegmentRecycler {

    public static final WastefulRecycler DEFAULT_INSTANCE = new WastefulRecycler(11, 8);
    public static final WastefulRecycler SMALL_ARRAY_RECYCLER = new WastefulRecycler(5, 2);

    private final int log2OfByteSegmentSize;
    private final int log2OfLongSegmentSize;

    public WastefulRecycler(int log2OfByteSegmentSize, int log2OfLongSegmentSize) {
        this.log2OfByteSegmentSize = log2OfByteSegmentSize;
        this.log2OfLongSegmentSize = log2OfLongSegmentSize;
    }

    @Override
    public int getLog2OfByteSegmentSize() {
        return log2OfByteSegmentSize;
    }

    @Override
    public int getLog2OfLongSegmentSize() {
        return log2OfLongSegmentSize;
    }

    @Override
    public long[] getLongArray() {
        return new long[(1 << log2OfLongSegmentSize) + 1];
    }

    @Override
    public byte[] getByteArray() {
        return new byte[(1 << log2OfByteSegmentSize)];
    }

    @Override
    public void recycleLongArray(long[] arr) {
        /// do nothing
    }

    @Override
    public void recycleByteArray(byte[] arr) {
        /// do nothing
    }

    @Override
    public void swap() {
        // do nothing
    }

}
