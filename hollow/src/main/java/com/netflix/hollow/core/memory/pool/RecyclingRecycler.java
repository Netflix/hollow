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

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

/**
 * A RecyclingRecycler is an {@link ArraySegmentRecycler} which actually pools arrays, in contrast
 * with a {@link WastefulRecycler}.
 */
public class RecyclingRecycler implements ArraySegmentRecycler {

    private final int log2OfByteSegmentSize;
    private final int log2OfLongSegmentSize;
    private final Recycler<long[]> longSegmentRecycler;
    private final Recycler<byte[]> byteSegmentRecycler;

    public RecyclingRecycler() {
        this(11, 8);
    }

    public RecyclingRecycler(final int log2ByteArraySize, final int log2LongArraySize) {
        this.log2OfByteSegmentSize = log2ByteArraySize;
        this.log2OfLongSegmentSize = log2LongArraySize;

        byteSegmentRecycler = new Recycler<>(() -> new byte[1 << log2ByteArraySize]);
        // Allocated size is increased by 1, see JavaDoc of FixedLengthElementArray for details
        longSegmentRecycler = new Recycler<>(() -> new long[(1 << log2LongArraySize) + 1]);
    }

    public int getLog2OfByteSegmentSize() {
        return log2OfByteSegmentSize;
    }

    public int getLog2OfLongSegmentSize() {
        return log2OfLongSegmentSize;
    }

    public long[] getLongArray() {
        long[] arr = longSegmentRecycler.get();
        Arrays.fill(arr, 0);
        return arr;
    }

    public void recycleLongArray(long[] arr) {
        longSegmentRecycler.recycle(arr);
    }

    public byte[] getByteArray() {
        // @@@ should the array be filled?
        return byteSegmentRecycler.get();
    }

    public void recycleByteArray(byte[] arr) {
        byteSegmentRecycler.recycle(arr);
    }

    public void swap() {
        longSegmentRecycler.swap();
        byteSegmentRecycler.swap();
    }


    private static class Recycler<T> {
        private final Creator<T> creator;
        private Deque<T> currentSegments;
        private Deque<T> nextSegments;

        Recycler(Creator<T> creator) {
            // Use an ArrayDeque instead of a LinkedList
            // This will avoid memory churn allocating and collecting internal nodes
            this.currentSegments = new ArrayDeque<>();
            this.nextSegments = new ArrayDeque<>();
            this.creator = creator;
        }

        T get() {
            if(!currentSegments.isEmpty()) {
                return currentSegments.removeFirst();
            }

            return creator.create();
        }

        void recycle(T reuse) {
            nextSegments.addLast(reuse);
        }

        void swap() {
            // Swap the deque references to reduce addition and clearing cost
            if(nextSegments.size() > currentSegments.size()) {
                Deque<T> tmp = nextSegments;
                nextSegments = currentSegments;
                currentSegments = tmp;
            }

            currentSegments.addAll(nextSegments);
            nextSegments.clear();
        }
    }

    private interface Creator<T> {
        T create();
    }

}
