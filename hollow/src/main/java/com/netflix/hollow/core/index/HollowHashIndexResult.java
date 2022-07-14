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
package com.netflix.hollow.core.index;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;
import java.util.Spliterator;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

/**
 * A HollowHashIndexResult contains the matches for a query to a {@link HollowHashIndex}.
 *
 */
public class HollowHashIndexResult {

    private final HollowHashIndex.HollowHashIndexState hashIndexState;
    private final long selectTableStartPointer;
    private final int selectTableSize;
    private final int selectTableBuckets;
    private final int selectBucketMask;

    HollowHashIndexResult(HollowHashIndex.HollowHashIndexState hashIndexState, long selectTableStartPointer, int selectTableSize) {
        this.hashIndexState = hashIndexState;
        this.selectTableStartPointer = selectTableStartPointer;
        this.selectTableSize = selectTableSize;
        this.selectTableBuckets = HashCodes.hashTableSize(selectTableSize);
        this.selectBucketMask = selectTableBuckets - 1;
    }

    /**
     * @return the number of matched records
     */
    public int numResults() {
        return selectTableSize;
    }

    /**
     * @param value the ordinal
     * @return {@code true} if the ordinal is matched, otherwise {@code false}
     */
    public boolean contains(int value) {
        int hash = HashCodes.hashInt(value);
        int bucket = hash & selectBucketMask;

        int selectOrdinal = (int) hashIndexState.getSelectHashArray().getElementValue((selectTableStartPointer + bucket) * hashIndexState.getBitsPerSelectHashEntry(), hashIndexState.getBitsPerSelectHashEntry()) - 1;
        while(selectOrdinal != -1) {
            if(selectOrdinal == value)
                return true;

            bucket = (bucket + 1) & selectBucketMask;
            selectOrdinal = (int) hashIndexState.getSelectHashArray().getElementValue((selectTableStartPointer + bucket) * hashIndexState.getBitsPerSelectHashEntry(), hashIndexState.getBitsPerSelectHashEntry()) - 1;
        }

        return false;
    }

    /**
     * @return A {@link HollowOrdinalIterator} over the matched ordinals.  The ordinals may be used with a generated API or the Generic Object API to inspect
     * the matched records.
     */
    public HollowOrdinalIterator iterator() {
        return new HollowOrdinalIterator() {
            final long endBucket = selectTableStartPointer + selectTableBuckets;
            long currentBucket = selectTableStartPointer;

            @Override
            public int next() {
                while(currentBucket < endBucket) {
                    int selectOrdinal = (int) hashIndexState.getSelectHashArray().getElementValue((currentBucket++) * hashIndexState.getBitsPerSelectHashEntry(), hashIndexState.getBitsPerSelectHashEntry()) - 1;
                    if(selectOrdinal != -1)
                        return selectOrdinal;
                }

                return NO_MORE_ORDINALS;
            }
        };
    }

    /**
     * Returns a stream of matching ordinals.
     * <p>
     * The ordinals may be used with a generated API or the Generic Object API to inspect
     * the matched records.
     *
     * @return an {@code IntStream} of matching ordinals
     */
    public IntStream stream() {
        Spliterator.OfInt si = new Spliterator.OfInt() {
            final long endBucket = selectTableStartPointer + selectTableBuckets;
            long currentBucket = selectTableStartPointer;

            @Override
            public OfInt trySplit() {
                // @@@ Supporting splitting and therefore enable parallelism
                return null;
            }

            @Override
            public boolean tryAdvance(IntConsumer action) {
                while(currentBucket < endBucket) {
                    int selectOrdinal = (int) hashIndexState.getSelectHashArray().getElementValue(
                            (currentBucket++) * hashIndexState.getBitsPerSelectHashEntry(),
                            hashIndexState.getBitsPerSelectHashEntry()) - 1;
                    if(selectOrdinal != -1) {
                        action.accept(selectOrdinal);
                        return true;
                    }
                }
                return false;
            }

            @Override
            public long estimateSize() {
                // @@@
                return 0;
            }

            @Override
            public int characteristics() {
                // @@@ ordinals are distinct?
                return 0;
            }
        };
        return StreamSupport.intStream(si, false);
    }
}
