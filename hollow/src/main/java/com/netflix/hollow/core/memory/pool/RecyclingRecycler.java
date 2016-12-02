/*
 *
 *  Copyright 2016 Netflix, Inc.
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

import java.util.Arrays;
import java.util.LinkedList;

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

        longSegmentRecycler = new Recycler<long[]>(new Creator<long[]>() {
            public long[] create() {
                return new long[(1 << log2LongArraySize) + 1];
            }
        });

        byteSegmentRecycler = new Recycler<byte[]>(new Creator<byte[]>() {
            public byte[] create() {
                return new byte[1 << log2ByteArraySize];
            }
        });
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
        return byteSegmentRecycler.get();
    }

    public void recycleByteArray(byte[] arr) {
        byteSegmentRecycler.recycle(arr);
    }

    public void swap() {
        longSegmentRecycler.swap();
        byteSegmentRecycler.swap();
    }


    private class Recycler<T> {

        private final Creator<T> creator;
        private final LinkedList<T> currentSegments;
        private final LinkedList<T> nextSegments;

        public Recycler(Creator<T> creator) {
            this.currentSegments = new LinkedList<T>();
            this.nextSegments = new LinkedList<T>();
            this.creator = creator;
        }

        public T get() {
            if(!currentSegments.isEmpty()) {
                return currentSegments.removeFirst();
            }

            return creator.create();
        }

        public void recycle(T reuse) {
            nextSegments.addLast(reuse);
        }

        public void swap() {
            currentSegments.addAll(nextSegments);
            nextSegments.clear();
        }
    }

    private interface Creator<T> {
        public T create();
    }

}
