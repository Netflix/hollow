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

import com.netflix.hollow.core.read.iterator.HollowOrdinalIterator;

/**
 * An ordinal iterator over a list record backed by a single validated snapshot of its shard and bounds.
 * <p>
 * The historical per-element pattern re-reads {@code shardsVolatile} and executes {@code Unsafe.loadFence()}
 * <em>twice</em> for every element (the inner loop validating {@code start}/{@code end} plus the trailing
 * validation of the element read), i.e. {@code 2N+1} fences for a list of size N including the initial
 * {@code size()}. This iterator instead validates the bounds once at construction and then executes a single
 * fence per {@link #next()}, i.e. {@code N+1} fences, while allocating nothing per element.
 * <p>
 * <b>Why holding the snapshot across {@code next()} calls is safe.</b> The bounds {@code startElement}/
 * {@code endElement} are validated non-torn before use, so every {@code listIndex < size} maps to a bit offset
 * within the shard's element region — an in-bounds, non-throwing read on-heap and within the buffer extent in
 * shared-memory mode. A concurrent delta or re-shard can only (a) publish a new shard (the captured reference is
 * untouched) and (b) later recycle the old shard's backing arrays, which are pooled but never truncated or
 * nulled — so a stale read yields garbage of the correct length, never an out-of-bounds access. The captured
 * {@code dataElements} reference also keeps that backing storage reachable for the life of the iterator. Each
 * {@link #next()} re-validates shard identity <em>after</em> reading (the {@code loadFence} in
 * {@link HollowListTypeReadState#readWasUnsafe} orders the element read before the re-read of
 * {@code shardsVolatile}); if the shard changed, the value read is discarded and the snapshot is re-established
 * against the newly observed version before the value is returned. Thus no torn value ever escapes, matching the
 * per-element validation semantics of the original iterator without its redundant second fence.
 */
class HollowListSnapshotOrdinalIterator implements HollowOrdinalIterator {

    private final HollowListTypeReadState readState;
    private final int ordinal;

    private HollowListTypeShardsHolder shardsHolder;
    private HollowListTypeReadStateShard shard;
    private long startElement;
    private long endElement;
    private int size;

    private int index;

    HollowListSnapshotOrdinalIterator(int ordinal, HollowListTypeReadState readState) {
        this.readState = readState;
        this.ordinal = ordinal;
        snapshot();
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
    public int next() {
        if(index >= size)
            return NO_MORE_ORDINALS;

        int listIndex = index;
        while(true) {
            // Bounds were validated in snapshot(), so listIndex < size is an in-bounds, non-throwing read.
            int elementOrdinal = shard.getElementOrdinal(startElement, endElement, listIndex);

            if(!readState.readWasUnsafe(shardsHolder, ordinal, shard)) {
                index++;
                return elementOrdinal;
            }

            // A delta or re-shard invalidated the snapshot mid-read; re-establish against the current version
            // and retry this index. The discarded value is never returned.
            snapshot();
            if(listIndex >= size)   // the record shrank in the newly observed version
                return NO_MORE_ORDINALS;
        }
    }

}
