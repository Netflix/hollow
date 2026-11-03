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
package com.netflix.hollow.api.consumer.longevity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.ObjectLongevity;
import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.core.read.dataaccess.HollowDataAccess;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.write.objectmapper.HollowPrimaryKey;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An in-memory producer whose versions change, remove and add records, reusing the ordinals that earlier versions
 * freed, and reference consumers that read each version exactly.
 */
final class HeldVersionsFixture {
    private static final HollowRecordStringifier STRINGIFIER = new HollowRecordStringifier(true, true, false);

    final InMemoryBlobStore blobStore = new InMemoryBlobStore();

    private final AtomicLong lastVersion = new AtomicLong();
    private final HollowProducer producer = HollowProducer.withPublisher(blobStore)
            .withBlobStager(new HollowInMemoryBlobStager())
            .withVersionMinter(() -> lastVersion.addAndGet(10)) // gaps, so versions between them exist in no transition
            .build();
    private final Random random = new Random(17);
    private final Map<Long, Movie> catalog = new TreeMap<>();
    private final Map<Long, HollowReadStateEngine> references = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, Map<Integer, String>>> records = new ConcurrentHashMap<>();
    private long nextId = 1;
    private int cycle;

    HeldVersionsFixture() {
        for (int i = 0; i < 12; i++)
            add();
    }

    /**
     * Publishes the next version: one record changes its title, another its tags and ratings, one is removed and one
     * is added.
     */
    long cycle() {
        cycle++;
        Movie retitled = pick();
        catalog.put(retitled.id, new Movie(retitled.id, "title " + retitled.id + "@" + cycle, retitled.tags, retitled.ratings));
        Movie retagged = pick();
        catalog.put(retagged.id, new Movie(retagged.id, retagged.title, tags(), ratings()));
        catalog.remove(pick().id);
        add();
        return producer.runCycle(ws -> catalog.values().forEach(ws::add));
    }

    List<Long> cycles(int count) {
        List<Long> versions = new ArrayList<>();
        for (int i = 0; i < count; i++)
            versions.add(cycle());
        return versions;
    }

    /**
     * Indexes of {@code count} versions in the order a consumer applies them: forward through all of them, back by
     * reverse deltas to {@code backTo}, and forward again.
     */
    static List<Integer> forwardBackForward(int count, int backTo) {
        List<Integer> path = new ArrayList<>();
        for (int i = 0; i < count; i++)
            path.add(i);
        for (int i = count - 2; i >= backTo; i--)
            path.add(i);
        for (int i = backTo + 1; i < count; i++)
            path.add(i);
        return path;
    }

    HollowConsumer.Builder<?> consumer() {
        return HollowConsumer.withBlobRetriever(blobStore);
    }

    HollowConsumer heldConsumer() {
        return consumer().withObjectLongevity(ObjectLongevity.held()).build();
    }

    /** Asserts that every record of {@code version} reads the same through {@code dataAccess}, at the same ordinal. */
    void assertReads(long version, HollowDataAccess dataAccess) {
        HollowReadStateEngine expected = reference(version);
        for (HollowTypeReadState typeState : expected.getTypeStates()) {
            String type = typeState.getSchema().getName();
            BitSet populated = typeState.getPopulatedOrdinals();
            for (int ordinal = populated.nextSetBit(0); ordinal >= 0; ordinal = populated.nextSetBit(ordinal + 1)) {
                assertEquals(type + " " + ordinal + " in " + version,
                        STRINGIFIER.stringify(expected, type, ordinal), STRINGIFIER.stringify(dataAccess, type, ordinal));
            }
        }
    }

    /** Asserts that {@code hold} reports exactly the populated ordinals of its version, for every type. */
    void assertPopulated(HollowVersionHold hold) {
        HollowReadStateEngine expected = reference(hold.getVersion());
        for (HollowTypeReadState typeState : expected.getTypeStates()) {
            String type = typeState.getSchema().getName();
            assertEquals(type + " in " + hold.getVersion(), typeState.getPopulatedOrdinals(), hold.getPopulatedOrdinals(type));
        }
    }

    /**
     * Asserts that {@code diff} is exact for the versions the consumer applied between its holds, listed from the
     * older hold's to the newer one's: an ordinal with the same record in all of them is neither removed nor added, and
     * any other ordinal is removed if the first version has it and added if the last one does.
     */
    void assertDiff(List<Long> between, HollowVersionDiff diff) {
        long from = between.get(0);
        long to = between.get(between.size() - 1);
        assertEquals(from, diff.getFromVersion());
        assertEquals(to, diff.getToVersion());
        assertTrue(diff.isIncremental());
        assertEquals(between.size() - 1, diff.getDeltaCount());
        Set<String> changed = new HashSet<>();
        for (String type : records(from).keySet()) {
            BitSet removed = diff.getRemovedOrdinals(type);
            BitSet added = diff.getAddedOrdinals(type);
            Set<Integer> ordinals = new TreeSet<>();
            for (long version : between)
                ordinals.addAll(records(version).get(type).keySet());
            removed.stream().forEach(ordinals::add);
            added.stream().forEach(ordinals::add);
            for (int ordinal : ordinals) {
                String first = records(from).get(type).get(ordinal);
                boolean same = first != null
                        && between.stream().allMatch(v -> first.equals(records(v).get(type).get(ordinal)));
                String context = type + " " + ordinal + " from " + from + " to " + to;
                assertEquals(context, !same && first != null, removed.get(ordinal));
                assertEquals(context, !same && records(to).get(type).containsKey(ordinal), added.get(ordinal));
            }
            if (!removed.isEmpty() || !added.isEmpty())
                changed.add(type);
        }
        assertEquals(changed, diff.getChangedTypes());
    }

    /** Asserts that the diff between two holds turns the older version's populated ordinals into the newer one's. */
    void assertDiffConnects(HollowVersionHold older, HollowVersionHold newer) {
        HollowVersionDiff diff = newer.diffFrom(older);
        if (!diff.isIncremental())
            return;
        for (HollowTypeReadState typeState : reference(newer.getVersion()).getTypeStates()) {
            String type = typeState.getSchema().getName();
            BitSet populated = new BitSet();
            populated.or(reference(older.getVersion()).getTypeState(type).getPopulatedOrdinals());
            populated.andNot(diff.getRemovedOrdinals(type));
            populated.or(diff.getAddedOrdinals(type));
            assertEquals(type + " from " + older.getVersion() + " to " + newer.getVersion(),
                    typeState.getPopulatedOrdinals(), populated);
        }
    }

    /**
     * Counts the records of {@code version} that read differently through {@code dataAccess}, for readers racing
     * refreshes. A read that overlapped two of the {@code transitionEvents}, which count each transition as it starts
     * and again once holds read its copies, is skipped: a read stalled from before one transition's copies are read
     * until after the next transition can miss a removed record, which the engine keeps for one transition only.
     */
    int mismatches(long version, HollowDataAccess dataAccess, AtomicLong transitionEvents) {
        HollowReadStateEngine expected = reference(version);
        int mismatches = 0;
        for (HollowTypeReadState typeState : expected.getTypeStates()) {
            String type = typeState.getSchema().getName();
            BitSet populated = typeState.getPopulatedOrdinals();
            for (int ordinal = populated.nextSetBit(0); ordinal >= 0; ordinal = populated.nextSetBit(ordinal + 1)) {
                String record = STRINGIFIER.stringify(expected, type, ordinal);
                long before = transitionEvents.get();
                String read = STRINGIFIER.stringify(dataAccess, type, ordinal);
                if (transitionEvents.get() - before <= 1 && !read.equals(record))
                    mismatches++;
            }
        }
        return mismatches;
    }

    HollowReadStateEngine reference(long version) {
        return references.computeIfAbsent(version, v -> {
            HollowConsumer reference = consumer().build();
            reference.triggerRefreshTo(v);
            return reference.getStateEngine();
        });
    }

    /** Every record of {@code version}, by type and ordinal. */
    private Map<String, Map<Integer, String>> records(long version) {
        return records.computeIfAbsent(version, v -> {
            HollowReadStateEngine engine = reference(v);
            Map<String, Map<Integer, String>> byType = new HashMap<>();
            for (HollowTypeReadState typeState : engine.getTypeStates()) {
                String type = typeState.getSchema().getName();
                Map<Integer, String> byOrdinal = new HashMap<>();
                typeState.getPopulatedOrdinals().stream()
                        .forEach(ordinal -> byOrdinal.put(ordinal, STRINGIFIER.stringify(engine, type, ordinal)));
                byType.put(type, byOrdinal);
            }
            return byType;
        });
    }

    private void add() {
        long id = nextId++;
        catalog.put(id, new Movie(id, "title " + id, tags(), ratings()));
    }

    private Movie pick() {
        List<Movie> movies = new ArrayList<>(catalog.values());
        return movies.get(random.nextInt(movies.size()));
    }

    private Set<String> tags() {
        Set<String> tags = new HashSet<>();
        for (int i = random.nextInt(3); i >= 0; i--)
            tags.add("tag " + random.nextInt(8));
        return tags;
    }

    private Map<String, Long> ratings() {
        Map<String, Long> ratings = new HashMap<>();
        for (int i = random.nextInt(3); i > 0; i--)
            ratings.put("region " + random.nextInt(4), (long) random.nextInt(5));
        return ratings;
    }

    @HollowPrimaryKey(fields = "id")
    static final class Movie {
        final long id;
        final String title;
        final Set<String> tags;
        final Map<String, Long> ratings;

        Movie(long id, String title, Set<String> tags, Map<String, Long> ratings) {
            this.id = id;
            this.title = title;
            this.tags = tags;
            this.ratings = ratings;
        }
    }
}
