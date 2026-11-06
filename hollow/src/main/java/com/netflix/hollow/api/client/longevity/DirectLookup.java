/*
 *  Copyright 2026 Netflix, Inc.
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
package com.netflix.hollow.api.client.longevity;

import com.netflix.hollow.core.memory.encoding.HashCodes;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.util.IntMap;
import com.netflix.hollow.tools.history.HollowHistoricalStateDataAccess;
import com.netflix.hollow.tools.history.HollowHistoricalTypeDataAccess;
import com.netflix.hollow.tools.history.IntMapOrdinalRemapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Held mode's index of the copies object longevity makes at each delta, so a read of a held version finds its record
 * with one lookup instead of walking the chain of copies.
 * <p>
 * For each type it keeps a bitset of the ordinals some copy holds, and for each of them the copies that hold its
 * earlier records, oldest first, each tagged with the position of the delta that removed it and the record's ordinal
 * in that copy. A version at position p reads an ordinal from the first copy after p, or from the engine if there is
 * none.
 * <p>
 * One thread indexes and prunes, between deltas: it indexes each delta before the next one starts, adding entries
 * before setting their bits, and prunes only entries no hold reads. Reads take no lock. A read that misses an entry
 * being indexed reads the engine, which keeps a removed record until the next delta, and entries reference their
 * copies directly, so a read racing a prune finds its own copy or none.
 */
final class DirectLookup {
    private final HollowReadStateEngine stateEngine;
    private final long since;
    private final Map<String, TypeLookup> types = new HashMap<>(); // the indexing thread's

    /**
     * @param stateEngine the state engine whose deltas this indexes
     * @param since the engine's position: deltas after it are indexed, so versions from it on can be read through this
     */
    DirectLookup(HollowReadStateEngine stateEngine, long since) {
        this.stateEngine = stateEngine;
        this.since = since;
    }

    long getSince() {
        return since;
    }

    /**
     * Indexes the copies of the delta at {@code position}.
     *
     * @param position the delta's position
     * @param copy the records the delta removed
     * @param removed the ordinals the delta removed, for each type it changed
     */
    void index(long position, HollowHistoricalStateDataAccess copy, Map<String, int[]> removed) {
        IntMapOrdinalRemapper copied = (IntMapOrdinalRemapper) copy.getOrdinalMapping();
        for (Map.Entry<String, int[]> changes : removed.entrySet()) {
            String type = changes.getKey();
            HollowHistoricalTypeDataAccess copiedType = (HollowHistoricalTypeDataAccess) copy.getTypeDataAccess(type);
            HollowTypeReadState records = copiedType.getRemovedRecords();
            IntMap copyOrdinals = copied.getOrdinalRemapping(type);
            TypeLookup lookup = type(type);
            for (int ordinal : changes.getValue()) {
                int copyOrdinal = copyOrdinals.get(ordinal);
                if (copyOrdinal < 0)
                    throw new IllegalStateException("Delta " + position + " has no copy of " + type + " " + ordinal);
                lookup.add(new Entry(ordinal, position, records, copyOrdinal));
            }
        }
        for (Map.Entry<String, int[]> changes : removed.entrySet()) {
            TypeLookup lookup = type(changes.getKey());
            for (int ordinal : changes.getValue())
                lookup.publish(ordinal); // only now: a reader that sees the bit finds the entry
        }
    }

    /**
     * Drops the entries of the delta at {@code position}, and any older ones of the same ordinals, once no hold reads
     * them.
     *
     * @param position the delta's position
     * @param removed the ordinals the delta removed, for each type it changed
     */
    void prune(long position, Map<String, int[]> removed) {
        for (Map.Entry<String, int[]> changes : removed.entrySet()) {
            TypeLookup lookup = types.get(changes.getKey());
            if (lookup != null) {
                for (int ordinal : changes.getValue())
                    lookup.dropThrough(ordinal, position);
            }
        }
    }

    /**
     * @param position a position from {@link #getSince()} on
     * @return a data access reading the version at {@code position}
     */
    HollowHeldStateDataAccess view(long position) {
        if (position < since)
            throw new IllegalArgumentException("Deltas before position " + since + " are not indexed: " + position);
        return new HollowHeldStateDataAccess(this, position);
    }

    HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    /** @return the ordinals, across types, that map to copies; read on the indexing thread */
    int ordinals() {
        int ordinals = 0;
        for (TypeLookup lookup : types.values())
            ordinals += lookup.size();
        return ordinals;
    }

    TypeLookup type(String type) {
        return types.computeIfAbsent(type, t -> new TypeLookup(stateEngine.getTypeState(t)));
    }

    /** A copy that holds an ordinal's record, from the delta that removed it. */
    static final class Entry {
        final int ordinal;
        final long position;
        final HollowTypeReadState copy;
        final int copyOrdinal;
        volatile Entry next; // from the next delta that removed this ordinal

        Entry(int ordinal, long position, HollowTypeReadState copy, int copyOrdinal) {
            this.ordinal = ordinal;
            this.position = position;
            this.copy = copy;
            this.copyOrdinal = copyOrdinal;
        }
    }

    private static final Entry TOMBSTONE = new Entry(-1, -1, null, -1); // a dropped ordinal's slot

    /** One type's bits and entries, in a table probed by ordinal whose slots hold each ordinal's oldest entry. */
    static final class TypeLookup {
        final HollowTypeReadState engine;
        private final Bits bits = new Bits();
        private volatile AtomicReferenceArray<Entry> slots = new AtomicReferenceArray<>(16);
        private int used; // slots holding an entry or a tombstone
        private int live; // ordinals with entries

        TypeLookup(HollowTypeReadState engine) {
            this.engine = engine;
        }

        /**
         * @return the entry of the first copy after {@code position} that holds the ordinal's record, or null if the
         * engine has it
         */
        Entry find(int ordinal, long position) {
            if (!bits.get(ordinal))
                return null;
            for (Entry entry = first(ordinal); entry != null; entry = entry.next) {
                if (entry.position > position)
                    return entry;
            }
            return null;
        }

        int size() {
            return live;
        }

        private Entry first(int ordinal) {
            AtomicReferenceArray<Entry> slots = this.slots;
            int mask = slots.length() - 1;
            for (int i = HashCodes.hashInt(ordinal) & mask; ; i = (i + 1) & mask) {
                Entry entry = slots.get(i);
                if (entry == null || entry.ordinal == ordinal)
                    return entry;
            }
        }

        /** Adds an entry, which readers find once its ordinal is published. */
        void add(Entry entry) {
            if ((used + 1) * 2 > slots.length())
                rehash();
            AtomicReferenceArray<Entry> slots = this.slots;
            int mask = slots.length() - 1;
            int free = -1;
            for (int i = HashCodes.hashInt(entry.ordinal) & mask; ; i = (i + 1) & mask) {
                Entry slot = slots.get(i);
                if (slot == null) {
                    if (free < 0) {
                        free = i;
                        used++;
                    }
                    live++;
                    slots.set(free, entry);
                    return;
                }
                if (slot == TOMBSTONE) {
                    if (free < 0)
                        free = i;
                } else if (slot.ordinal == entry.ordinal) {
                    while (slot.next != null)
                        slot = slot.next;
                    slot.next = entry;
                    return;
                }
            }
        }

        void publish(int ordinal) {
            bits.set(ordinal);
        }

        /** Drops the ordinal's entries from deltas up to {@code position}, and its bit once it has none. */
        void dropThrough(int ordinal, long position) {
            AtomicReferenceArray<Entry> slots = this.slots;
            int mask = slots.length() - 1;
            for (int i = HashCodes.hashInt(ordinal) & mask; ; i = (i + 1) & mask) {
                Entry slot = slots.get(i);
                if (slot == null)
                    return;
                if (slot.ordinal == ordinal) {
                    Entry first = slot;
                    while (first != null && first.position <= position)
                        first = first.next;
                    if (first != null) {
                        slots.set(i, first);
                    } else {
                        slots.set(i, TOMBSTONE);
                        live--;
                        bits.clear(ordinal);
                    }
                    return;
                }
            }
        }

        /** Readers of the old table still find every entry it had: later changes go to the new one. */
        private void rehash() {
            AtomicReferenceArray<Entry> old = slots;
            int capacity = 16;
            while (capacity < (live + 1) * 4)
                capacity <<= 1;
            AtomicReferenceArray<Entry> grown = new AtomicReferenceArray<>(capacity);
            int mask = capacity - 1;
            for (int i = 0; i < old.length(); i++) {
                Entry entry = old.get(i);
                if (entry != null && entry != TOMBSTONE) {
                    int j = HashCodes.hashInt(entry.ordinal) & mask;
                    while (grown.get(j) != null)
                        j = (j + 1) & mask;
                    grown.set(j, entry);
                }
            }
            used = live;
            slots = grown;
        }
    }

    /** A bitset one thread writes while others read it. */
    private static final class Bits {
        private volatile AtomicLongArray words = new AtomicLongArray(1);

        boolean get(int bit) {
            AtomicLongArray words = this.words;
            int word = bit >>> 6;
            return word < words.length() && (words.get(word) & (1L << bit)) != 0;
        }

        void set(int bit) {
            int word = bit >>> 6;
            AtomicLongArray words = this.words;
            if (word >= words.length()) {
                AtomicLongArray grown = new AtomicLongArray(Math.max(word + 1, words.length() * 2));
                for (int i = 0; i < words.length(); i++)
                    grown.set(i, words.get(i));
                this.words = words = grown;
            }
            words.set(word, words.get(word) | (1L << bit));
        }

        void clear(int bit) {
            AtomicLongArray words = this.words;
            int word = bit >>> 6;
            if (word < words.length())
                words.set(word, words.get(word) & ~(1L << bit));
        }
    }
}
