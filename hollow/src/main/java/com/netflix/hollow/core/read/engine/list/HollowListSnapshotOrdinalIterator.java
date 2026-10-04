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
package com.netflix.hollow.core.read.engine.list;

import com.netflix.hollow.api.sampling.HollowListSampler;
import com.netflix.hollow.core.read.iterator.HollowListOrdinalIterator;

/**
 * An ordinal iterator over a list record backed by a single validated snapshot of its shard and bounds.
 * <p>
 * When on-heap arrays can be recycled, per-element access re-reads {@code shardsVolatile} and executes
 * {@code Unsafe.loadFence()} <em>twice</em> for every element (the inner loop validating {@code start}/{@code end} plus the trailing
 * validation of the element read), i.e. {@code 2N+1} fences for a list of size N including the initial
 * {@code size()}. This iterator instead validates the bounds once at construction and then executes a single
 * fence per {@link #next()}, i.e. {@code N+1} fences, while allocating nothing per element.
 * <p>
 * <b>Why holding the snapshot across {@code next()} calls is safe.</b> The bounds {@code startElement}/
 * {@code endElement} are validated before use, so every {@code listIndex < size} maps to an in-bounds read from
 * the captured shard. During an on-heap update, the captured data remains strongly reachable and recycled arrays
 * retain their fixed size. Each {@link #next()} re-validates shard identity <em>after</em> reading; if an update or
 * reshard replaced it, the value is discarded and the snapshot is re-established before a value is returned.
 * <p>
 * When retired on-heap arrays are not recycled, the captured shard remains immutable and strongly reachable.
 * In that case {@code readWasUnsafe} is a no-op, so the iterator retains its initial snapshot without fences.
 */
final class HollowListSnapshotOrdinalIterator extends HollowListOrdinalIterator {

    private final HollowListTypeReadState readState;
    private final int ordinal;

    private HollowListTypeShardsHolder shardsHolder;
    private HollowListTypeReadStateShard shard;
    private long startElement;
    private long endElement;
    private int size;

    private int index;

    HollowListSnapshotOrdinalIterator(
            int ordinal, HollowListTypeReadState readState, HollowListSampler sampler) {
        this.readState = readState;
        this.ordinal = ordinal;
        sampler.recordSize();
        snapshot();
    }

    private HollowListSnapshotOrdinalIterator(HollowListSnapshotOrdinalIterator source) {
        this.readState = source.readState;
        this.ordinal = source.ordinal;
        this.shardsHolder = source.shardsHolder;
        this.shard = source.shard;
        this.startElement = source.startElement;
        this.endElement = source.endElement;
        this.size = source.size;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public HollowListOrdinalIterator copy() {
        // Splits retain the captured range, but shard refreshes must remain local to each reader.
        return new HollowListSnapshotOrdinalIterator(this);
    }

    /**
     * Establish a validated snapshot: a shard whose {@code startElement}/{@code endElement} for this ordinal were
     * read while the shard was current (hence non-torn). Mirrors the inner validation loop of
     * {@link HollowListTypeReadState#getElementOrdinal(int, int)}.
     */
    private void snapshot() {
        HollowListTypeShardsHolder holder;
        HollowListTypeReadStateShard s;
        long start;
        long end;
        do {
            holder = readState.shardsVolatile;
            s = holder.shards[ordinal & holder.shardNumberMask];
            int shardOrdinal = ordinal >> s.shardOrdinalShift;

            start = s.dataElements.getStartElement(shardOrdinal);
            end = s.dataElements.getEndElement(shardOrdinal);
        } while(readState.readWasUnsafe(holder, ordinal, s));

        this.shardsHolder = holder;
        this.shard = s;
        this.startElement = start;
        this.endElement = end;
        this.size = (int)(end - start);
    }

    @Override
    public int getElementOrdinal(int listIndex) {
        int elementOrdinal = readElement(listIndex);
        if(elementOrdinal == NO_MORE_ORDINALS)
            throw new IndexOutOfBoundsException("Index: " + listIndex + ", size: " + size);
        return elementOrdinal;
    }

    private int readElement(int listIndex) {
        while(true) {
            if(listIndex < 0 || listIndex >= size)
                return NO_MORE_ORDINALS;
            // The captured bounds keep the speculative read in bounds even during array recycling.
            int elementOrdinal = shard.getElementOrdinal(startElement, endElement, listIndex);
            if(!readState.readWasUnsafe(shardsHolder, ordinal, shard))
                return elementOrdinal;
            snapshot();
        }
    }

    @Override
    public int next() {
        int elementOrdinal = readElement(index);
        if(elementOrdinal != NO_MORE_ORDINALS)
            index++;
        return elementOrdinal;
    }

}
