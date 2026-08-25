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
package com.netflix.hollow.api.consumer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.HollowProducer.ReadState;
import com.netflix.hollow.api.producer.HollowProducer.VersionMinter;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.validation.ValidationResult;
import com.netflix.hollow.api.producer.validation.ValidationResultType;
import com.netflix.hollow.api.producer.validation.ValidationStatus;
import com.netflix.hollow.api.producer.validation.ValidationStatusException;
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import com.netflix.hollow.api.producer.validation.ValidatorListener;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.engine.object.HollowObjectTypeReadState;
import com.netflix.hollow.core.read.filter.TypeFilter;
import com.netflix.hollow.test.InMemoryBlobStore;
import com.netflix.hollow.tools.checksum.HollowChecksum;
import com.netflix.hollow.tools.compact.HollowCompactor.CompactionConfig;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HollowProducerConsumerTests {

    private InMemoryBlobStore blobStore;
    private InMemoryAnnouncement announcement;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
        announcement = new InMemoryAnnouncement();
    }

    @Test
    public void publishAndLoadASnapshot() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        /// Showing verbose version of `runCycle(producer, 1);`
        long version = producer.runCycle(state -> state.add(1));

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(version);

        Assert.assertEquals(version, consumer.getCurrentVersionId());
    }

    @Test
    public void initializationTraversesDeltasToGetUpToDate() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshots for v2 or v3
                .build();

        long v1 = runCycle(producer, 1);
        long v2 = runCycle(producer, 2);
        long v3 = runCycle(producer, 3);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v3);

        Assert.assertEquals(v3, consumer.getCurrentVersionId());

        Assert.assertEquals(v1, blobStore.retrieveSnapshotBlob(v3).getToVersion());
        Assert.assertEquals(v2, blobStore.retrieveDeltaBlob(v1).getToVersion());
        Assert.assertEquals(v3, blobStore.retrieveDeltaBlob(v2).getToVersion());
    }

    @Test
    public void consumerAutomaticallyUpdatesBasedOnAnnouncement() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withAnnouncer(announcement)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = runCycle(producer, 1);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withAnnouncementWatcher(announcement)
                .build();
        consumer.triggerRefresh();

        Assert.assertEquals(v1, consumer.getCurrentVersionId());

        long v2 = runCycle(producer, 2);

        Assert.assertEquals(v2, consumer.getCurrentVersionId());
    }

    @Test
    public void consumerFollowsReverseDeltas() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshot for v2 or v3
                .build();

        long v1 = runCycle(producer, 1);
        runCycle(producer, 2);
        long v3 = runCycle(producer, 3);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v3);

        Assert.assertEquals(v3, consumer.getCurrentVersionId());

        blobStore.removeSnapshot(
                v1); // <-- not necessary to cause following of reverse deltas -- just asserting that's what happened.
        consumer.triggerRefreshTo(v1);

        Assert.assertEquals(v1, consumer.getCurrentVersionId());
    }

    @Test
    public void consumerRespondsToPinnedAnnouncement() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withAnnouncer(announcement)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withNumStatesBetweenSnapshots(2) /// do not produce snapshot for v2 or v3
                .build();

        long v1 = runCycle(producer, 1);
        runCycle(producer, 2);
        long v3 = runCycle(producer, 3);


        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withAnnouncementWatcher(announcement)
                .build();
        consumer.triggerRefresh();

        Assert.assertEquals(v3, consumer.getCurrentVersionId());

        announcement.pin(v1);

        Assert.assertEquals(v1, consumer.getCurrentVersionId());

        /// another cycle occurs while we're pinned
        long v4 = runCycle(producer, 4);

        announcement.unpin();

        Assert.assertEquals(v4, consumer.getCurrentVersionId());
    }

    @Test
    public void consumerFindsLatestPublishedVersionWithoutAnnouncementWatcher() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withAnnouncer(announcement)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = runCycle(producer, 1);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();

        consumer.triggerRefresh();
        Assert.assertEquals(v1, consumer.getCurrentVersionId());

        consumer.triggerRefresh();
        Assert.assertEquals(v1, consumer.getCurrentVersionId());

        long v2 = runCycle(producer, 2);

        consumer.triggerRefresh();
        Assert.assertEquals(v2, consumer.getCurrentVersionId());
    }

    @Test
    public void producerRestoresAndProducesDelta() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v1 = runCycle(producer, 1);

        HollowProducer redeployedProducer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        redeployedProducer.initializeDataModel(Integer.class);
        redeployedProducer.restore(v1, blobStore);

        long v2 = runCycle(producer, 2);

        Assert.assertNotNull(blobStore.retrieveDeltaBlob(v1));
        Assert.assertEquals(v2, blobStore.retrieveDeltaBlob(v1).getToVersion());
    }

    @Test
    public void producerUsesCustomSnapshotPublisherExecutor() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withSnapshotPublishExecutor(command -> {
                    /// do not publish snapshots!
                })
                .build();

        long v1 = runCycle(producer, 1);
        long v2 = runCycle(producer, 2);
        long v3 = runCycle(producer, 3);
        long v4 = runCycle(producer, 4);

        /// first cycle always publishes in-band -- does not use the Executor, so we expect a snapshot for v1.
        Assert.assertEquals(v1, blobStore.retrieveSnapshotBlob(v1).getToVersion());
        Assert.assertEquals(v1, blobStore.retrieveSnapshotBlob(v2).getToVersion());
        Assert.assertEquals(v1, blobStore.retrieveSnapshotBlob(v3).getToVersion());
        Assert.assertEquals(v1, blobStore.retrieveSnapshotBlob(v4).getToVersion());
    }

    @Test
    public void producerUsesCustomVersionMinter() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withVersionMinter(new VersionMinter() {
                    long counter = 0;

                    public long mint() {
                        return ++counter;
                    }
                })
                .build();

        long v1 = runCycle(producer, 1);
        long v2 = runCycle(producer, 2);
        long v3 = runCycle(producer, 3);

        Assert.assertEquals(1, v1);
        Assert.assertEquals(2, v2);
        Assert.assertEquals(3, v3);
    }

    @Test
    public void producerValidatesWithFailure() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new ValidatorListener() {
                    @Override public String getName() {
                        return "Test validator";
                    }

                    @Override public ValidationResult onValidate(ReadState readState) {
                        return ValidationResult.from(this).failed("Expected to fail!");
                    }
                })
                .withListener(new ValidationStatusListener() {
                    boolean isStartCalled;

                    @Override public void onValidationStatusStart(long version) {
                        isStartCalled = true;
                    }

                    @Override public void onValidationStatusComplete(
                            ValidationStatus status, long version, Duration elapsed) {
                        Assert.assertTrue(isStartCalled);
                        Assert.assertTrue(status.failed());
                        Assert.assertEquals(1, status.getResults().size());

                        ValidationResult r = status.getResults().get(0);
                        Assert.assertEquals("Test validator", r.getName());
                        Assert.assertEquals("Expected to fail!", r.getMessage());
                        Assert.assertEquals(ValidationResultType.FAILED, r.getResultType());
                    }
                })
                .build();

        try {
            runCycle(producer, 1);
            Assert.fail();
        } catch (ValidationStatusException expected) {
            ValidationStatus status = expected.getValidationStatus();
            Assert.assertTrue(status.failed());
            Assert.assertEquals(1, status.getResults().size());

            ValidationResult r = status.getResults().get(0);
            Assert.assertEquals("Test validator", r.getName());
            Assert.assertEquals("Expected to fail!", r.getMessage());
            Assert.assertEquals(ValidationResultType.FAILED, r.getResultType());
        }
    }

    @Test
    public void producerValidatesWithError() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new ValidatorListener() {
                    @Override public String getName() {
                        return "Test validator";
                    }

                    @Override public ValidationResult onValidate(ReadState readState) {
                        throw new RuntimeException("Expected to fail!");
                    }
                })
                .withListener(new ValidationStatusListener() {
                    boolean isStartCalled;

                    @Override public void onValidationStatusStart(long version) {
                        isStartCalled = true;
                    }

                    @Override public void onValidationStatusComplete(
                            ValidationStatus status, long version, Duration elapsed) {
                        Assert.assertTrue(isStartCalled);
                        Assert.assertTrue(status.failed());
                        Assert.assertEquals(1, status.getResults().size());

                        ValidationResult r = status.getResults().get(0);
                        Assert.assertEquals("Test validator", r.getName());
                        Assert.assertEquals("Expected to fail!", r.getMessage());
                        Assert.assertEquals(ValidationResultType.ERROR, r.getResultType());
                    }
                })
                .build();

        try {
            runCycle(producer, 1);
            Assert.fail();
        } catch (ValidationStatusException expected) {
            ValidationStatus status = expected.getValidationStatus();
            Assert.assertTrue(status.failed());
            Assert.assertEquals(1, status.getResults().size());

            ValidationResult r = status.getResults().get(0);
            Assert.assertEquals("Test validator", r.getName());
            Assert.assertEquals("Expected to fail!", r.getMessage());
            Assert.assertEquals(ValidationResultType.ERROR, r.getResultType());
        }
    }

    @Test
    public void producerCanContinueAfterValidationFailureNew() {
        AtomicInteger counter = new AtomicInteger();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withListener(new ValidatorListener() {
                    @Override
                    public String getName() {
                        return "Test validator";
                    }

                    @Override
                    public ValidationResult onValidate(ReadState readState) {
                        if (counter.incrementAndGet() == 2) {
                            return ValidationResult.from(this).failed("Expected to fail!");
                        } else {
                            return ValidationResult.from(this).passed("Pass");
                        }
                    }
                })
                .withListener(new ValidationStatusListener() {
                    @Override public void onValidationStatusStart(long version) {
                    }

                    @Override public void onValidationStatusComplete(
                            ValidationStatus status, long version, Duration elapsed) {
                        if (counter.get() == 2) {
                            Assert.assertTrue(status.failed());
                            Assert.assertEquals(1, status.getResults().size());

                            ValidationResult r = status.getResults().get(0);
                            Assert.assertEquals("Test validator", r.getName());
                            Assert.assertEquals("Expected to fail!", r.getMessage());
                            Assert.assertEquals(ValidationResultType.FAILED, r.getResultType());
                        } else {
                            Assert.assertTrue(status.passed());
                            Assert.assertEquals(1, status.getResults().size());

                            ValidationResult r = status.getResults().get(0);
                            Assert.assertEquals("Test validator", r.getName());
                            Assert.assertEquals("Pass", r.getMessage());
                            Assert.assertEquals(ValidationResultType.PASSED, r.getResultType());
                        }
                    }
                })
                .build();

        runCycle(producer, 1);

        try {
            runCycle(producer, 2);
            Assert.fail();
        } catch (ValidationStatusException expected) {
            ValidationStatus status = expected.getValidationStatus();
            Assert.assertTrue(status.failed());
            Assert.assertEquals(1, status.getResults().size());

            ValidationResult r = status.getResults().get(0);
            Assert.assertEquals("Test validator", r.getName());
            Assert.assertEquals("Expected to fail!", r.getMessage());
            Assert.assertEquals(ValidationResultType.FAILED, r.getResultType());
        }

        runCycle(producer, 3);
    }

    @Test
    public void producerCompacts() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add(i);
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add(i);
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);

        /// assert that a compaction is now necessary
        long popOrdinalsLength = consumer.getStateEngine().getTypeState("Integer").getPopulatedOrdinals().length();
        Assert.assertEquals(20000, popOrdinalsLength);

        /// run a compaction cycle
        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 20));

        /// assert that a compaction actually happened
        consumer.triggerRefreshTo(v3);
        popOrdinalsLength = consumer.getStateEngine().getTypeState("Integer").getPopulatedOrdinals().length();
        Assert.assertEquals(10000, popOrdinalsLength);

        BitSet foundValues = new BitSet(20000);
        for (int i = 0; i < popOrdinalsLength; i++) {
            foundValues.set(((HollowObjectTypeReadState) consumer.getStateEngine().getTypeState("Integer"))
                    .readInt(i, 0));
        }

        for (int i = 10000; i < 20000; i++) {
            Assert.assertTrue(foundValues.get(i));
        }
    }

    @Test
    public void producerCompactsGraduallyWithinADeltaByteBudget() throws IOException {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add(i);
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add(i);
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "Integer"));

        /// small enough that a single cycle cannot possibly reclaim all 10000 holes
        CompactionConfig config = new CompactionConfig(0, 0, 4096);

        long fromVersion = v2;
        int previousLength = 20000;
        int cycles = 0;

        while (true) {
            long version = producer.runCompactionCycle(config);
            if (version == HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE) {
                break;
            }

            /// the budget bounds what each individual delta carries
            Assert.assertTrue("delta from " + fromVersion + " exceeded the budget",
                    deltaBlobSizeInBytes(fromVersion) <= 4096 * 4);

            consumer.triggerRefreshTo(version);
            int length = populatedOrdinalsLength(consumer, "Integer");

            /// every cycle makes strict progress, so this terminates
            Assert.assertTrue("cycle " + cycles + " did not shrink the ordinal space", length < previousLength);

            previousLength = length;
            fromVersion = version;
            cycles++;
            Assert.assertTrue("compaction is not converging", cycles < 500);
        }

        /// converged all the way down, and it genuinely took multiple cycles to get there
        Assert.assertEquals(10000, previousLength);
        Assert.assertTrue("expected a gradual compaction, took " + cycles + " cycle(s)", cycles > 1);

        /// every record survived the gradual relocation
        BitSet foundValues = new BitSet(20000);
        HollowObjectTypeReadState typeState =
                (HollowObjectTypeReadState) consumer.getStateEngine().getTypeState("Integer");
        for (int i = 0; i < 10000; i++) {
            foundValues.set(typeState.readInt(i, 0));
        }

        for (int i = 10000; i < 20000; i++) {
            Assert.assertTrue(foundValues.get(i));
        }
    }

    @Test
    public void deltaByteBudgetAccountsForTheReferencingClosure() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add(new Movie(i, "title" + i));
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add(new Movie(i, "title" + i));
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "String"));
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "Movie"));

        /// String is the compaction target; each relocated String drags its referencing Movie into the delta too
        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 0, 8192));
        consumer.triggerRefreshTo(v3);

        int stringRelocations = 20000 - populatedOrdinalsLength(consumer, "String");
        int movieChurn = 20000 - populatedOrdinalsLength(consumer, "Movie");

        Assert.assertTrue("expected progress on the compaction target", stringRelocations > 0);
        Assert.assertEquals("each relocated String should have churned exactly one referencing Movie",
                stringRelocations, movieChurn);

        /// the same budget spent on a type with no referencers buys strictly more relocations, because none of it
        /// is consumed by closure churn
        Assert.assertTrue("closure churn should have reduced the batch size",
                stringRelocations < unreferencedTypeRelocationsForSameBudget());
    }

    @Test
    public void aBudgetTooSmallForOneRecordSkipsCompactionEntirely() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add(new Movie(i, "title" + i));
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add(new Movie(i, "title" + i));
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);

        /// one byte cannot cover a single record plus its closure, so no cycle should be produced at all
        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 0, 1));
        Assert.assertEquals(HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE, v3);

        /// no version was announced, and nothing was relocated
        consumer.triggerRefresh();
        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "String"));
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "Movie"));

        /// and a budget that does fit still compacts, so the skip was the budget's doing and not a stuck state
        long v4 = producer.runCompactionCycle(new CompactionConfig(0, 0, 8192));
        Assert.assertNotEquals(HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE, v4);
        consumer.triggerRefreshTo(v4);
        Assert.assertTrue(populatedOrdinalsLength(consumer, "String") < 20000);
    }

    /// how many records a budget of 8192 buys for a type nothing references, as a baseline for the closure test
    private int unreferencedTypeRelocationsForSameBudget() {
        InMemoryBlobStore standaloneStore = new InMemoryBlobStore();
        HollowProducer producer = HollowProducer.withPublisher(standaloneStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add("title" + i);
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add("title" + i);
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(standaloneStore).build();
        consumer.triggerRefreshTo(v2);

        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 0, 8192));
        consumer.triggerRefreshTo(v3);

        return 20000 - populatedOrdinalsLength(consumer, "String");
    }

    private long deltaBlobSizeInBytes(long fromVersion) throws IOException {
        try (InputStream is = blobStore.retrieveDeltaBlob(fromVersion).getInputStream()) {
            long size = 0;
            int read;
            byte[] buf = new byte[8192];
            while ((read = is.read(buf)) != -1) {
                size += read;
            }
            return size;
        }
    }

    /**
     * The strongest guard available: a state reached by following compaction deltas must be ordinal-for-ordinal
     * identical to the same version loaded fresh from a snapshot.  HollowChecksum folds the ordinal of every record
     * of every type into the result, so this covers the remapping of the compaction target and its entire closure.
     */
    @Test
    public void compactedStateIsIdenticalWhetherLoadedBySnapshotOrDelta() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        publishFragmentedMovies(producer);

        HollowConsumer deltaConsumer = HollowConsumer.withBlobRetriever(blobStore).build();
        deltaConsumer.triggerRefresh();

        int cycles = compactToConvergence(producer, deltaConsumer, new CompactionConfig(0, 0, 8192));
        Assert.assertTrue("expected a gradual compaction, took " + cycles + " cycle(s)", cycles > 1);

        HollowConsumer snapshotConsumer = HollowConsumer.withBlobRetriever(blobStore).build();
        snapshotConsumer.triggerRefreshTo(deltaConsumer.getCurrentVersionId());
        Assert.assertEquals(deltaConsumer.getCurrentVersionId(), snapshotConsumer.getCurrentVersionId());

        Assert.assertEquals(HollowChecksum.forStateEngine(snapshotConsumer.getStateEngine()),
                HollowChecksum.forStateEngine(deltaConsumer.getStateEngine()));
    }

    @Test
    public void gradualCompactionPreservesReferencesThroughTheClosure() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v2 = publishFragmentedMovies(producer);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);
        assertMoviesIntact(consumer);

        compactToConvergence(producer, consumer, new CompactionConfig(0, 0, 8192));

        /// every Movie still resolves to its own title, despite both types having been remapped piecemeal
        assertMoviesIntact(consumer);
    }

    @Test
    public void gradualCompactionPreservesCollectionTypes() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 3000; i++) {
                state.add(new Catalog(i));
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 3000; i < 6000; i++) {
                state.add(new Catalog(i));
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);

        /// the LIST, SET and MAP copiers all run through PartialOrdinalRemapper during compaction
        Assert.assertNotNull(consumer.getStateEngine().getTypeState("ListOfString"));
        Assert.assertNotNull(consumer.getStateEngine().getTypeState("SetOfInteger"));
        Assert.assertNotNull(consumer.getStateEngine().getTypeState("MapOfStringToInteger"));

        int cycles = compactToConvergence(producer, consumer, new CompactionConfig(0, 0, 8192));
        Assert.assertTrue("expected a gradual compaction, took " + cycles + " cycle(s)", cycles > 1);

        HollowConsumer snapshotConsumer = HollowConsumer.withBlobRetriever(blobStore).build();
        snapshotConsumer.triggerRefreshTo(consumer.getCurrentVersionId());
        Assert.assertEquals(HollowChecksum.forStateEngine(snapshotConsumer.getStateEngine()),
                HollowChecksum.forStateEngine(consumer.getStateEngine()));

        Assert.assertEquals(3000, consumer.getStateEngine().getTypeState("Catalog")
                .getPopulatedOrdinals().cardinality());
    }

    @Test
    public void gradualCompactionWorksOnShardedTypes() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withTargetMaxTypeShardSize(4096) /// force the types to shard
                .build();

        publishFragmentedMovies(producer);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();
        Assert.assertTrue("expected a sharded type",
                consumer.getStateEngine().getTypeState("Movie").numShards() > 1);

        compactToConvergence(producer, consumer, new CompactionConfig(0, 0, 8192));

        assertMoviesIntact(consumer);

        HollowConsumer snapshotConsumer = HollowConsumer.withBlobRetriever(blobStore).build();
        snapshotConsumer.triggerRefreshTo(consumer.getCurrentVersionId());
        Assert.assertEquals(HollowChecksum.forStateEngine(snapshotConsumer.getStateEngine()),
                HollowChecksum.forStateEngine(consumer.getStateEngine()));
    }

    @Test
    public void compactionCyclesInterleaveWithDataCycles() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        publishFragmentedMovies(producer);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();

        CompactionConfig config = new CompactionConfig(0, 0, 8192);
        int compactions = 0;

        /// alternate ordinary data cycles with budgeted compaction cycles, the way a producer actually would
        for (int round = 0; round < 5; round++) {
            int generation = round;
            long dataVersion = producer.runCycle(state -> {
                for (int i = 10000; i < 20000; i++) {
                    state.add(new Movie(i, "title" + i));
                }
                state.add(new Movie(-1, "generation" + generation));
            });
            consumer.triggerRefreshTo(dataVersion);

            long compactionVersion = producer.runCompactionCycle(config);
            if (compactionVersion != HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE) {
                consumer.triggerRefreshTo(compactionVersion);
                compactions++;
            }

            assertMoviesIntact(consumer, "generation" + generation);
        }

        Assert.assertTrue("no compaction ran, so the interleaving was never exercised", compactions > 0);
    }

    @Test
    public void consumerCanFollowReverseDeltaAcrossACompaction() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        long v2 = publishFragmentedMovies(producer);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);
        HollowChecksum beforeCompaction = HollowChecksum.forStateEngine(consumer.getStateEngine());

        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 0, 8192));
        Assert.assertNotEquals(HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE, v3);
        consumer.triggerRefreshTo(v3);

        /// walking the reverse delta back across the compaction must restore the pre-compaction ordinals exactly
        blobStore.removeSnapshot(v2);
        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(v2, consumer.getCurrentVersionId());
        Assert.assertEquals(beforeCompaction, HollowChecksum.forStateEngine(consumer.getStateEngine()));
        assertMoviesIntact(consumer);
    }

    @Test
    public void budgetedCompactionTerminatesAtTheHolePercentageThreshold() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        publishFragmentedMovies(producer);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();

        /// a non-zero threshold must stop the loop short of a fully compacted state rather than spin forever
        compactToConvergence(producer, consumer, new CompactionConfig(0, 25, 8192));

        int length = populatedOrdinalsLength(consumer, "String");
        Assert.assertTrue("should have compacted below the starting 50% holes, was " + length, length < 20000);
        Assert.assertTrue("should have stopped at the threshold rather than fully compacting, was " + length,
                length > 10000);
    }

    @Test
    public void anUnbudgetedConfigCompactsInASingleCycle() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        publishFragmentedMovies(producer);

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefresh();

        /// the two-arg config must remain equivalent to an unbounded budget
        Assert.assertEquals(Long.MAX_VALUE, new CompactionConfig(0, 0).getApproxDeltaBytesPerCycle());

        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 0));
        consumer.triggerRefreshTo(v3);
        Assert.assertEquals(10000, populatedOrdinalsLength(consumer, "String"));
        assertMoviesIntact(consumer);
    }

    @Test
    public void aNonPositiveBudgetIsRejected() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        publishFragmentedMovies(producer);

        try {
            producer.runCompactionCycle(new CompactionConfig(0, 0, 0));
            Assert.fail("expected a non-positive budget to be rejected");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(expected.getMessage().contains("approxDeltaBytesPerCycle"));
        }
    }

    private long publishFragmentedMovies(HollowProducer producer) {
        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add(new Movie(i, "title" + i));
            }
        });

        /// drops the first 10000, leaving both Movie and String half holes
        return producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add(new Movie(i, "title" + i));
            }
        });
    }

    private int compactToConvergence(HollowProducer producer, HollowConsumer consumer, CompactionConfig config) {
        int cycles = 0;

        while (true) {
            long version = producer.runCompactionCycle(config);
            if (version == HollowConsumer.AnnouncementWatcher.NO_ANNOUNCEMENT_AVAILABLE) {
                return cycles;
            }

            consumer.triggerRefreshTo(version);
            cycles++;
            Assert.assertTrue("compaction is not converging", cycles < 500);
        }
    }

    private static void assertMoviesIntact(HollowConsumer consumer, String... extraTitles) {
        HollowObjectTypeReadState movies =
                (HollowObjectTypeReadState) consumer.getStateEngine().getTypeState("Movie");
        HollowObjectTypeReadState strings =
                (HollowObjectTypeReadState) consumer.getStateEngine().getTypeState("String");
        int idField = movies.getSchema().getPosition("id");
        int titleField = movies.getSchema().getPosition("title");

        BitSet seenIds = new BitSet(20000);
        int extrasSeen = 0;
        BitSet populated = movies.getPopulatedOrdinals();
        int ordinal = populated.nextSetBit(0);

        while (ordinal != -1) {
            int id = movies.readInt(ordinal, idField);
            String title = strings.readString(movies.readOrdinal(ordinal, titleField), 0);

            if (id == -1) {
                Assert.assertEquals(extraTitles[0], title);
                extrasSeen++;
            } else {
                Assert.assertEquals("Movie " + id + " lost its title", "title" + id, title);
                seenIds.set(id);
            }

            ordinal = populated.nextSetBit(ordinal + 1);
        }

        Assert.assertEquals(10000, seenIds.cardinality());
        Assert.assertEquals(10000, seenIds.nextSetBit(0));
        Assert.assertEquals(extraTitles.length, extrasSeen);
    }

    @SuppressWarnings("unused")
    private static class Movie {
        private final int id;
        private final String title;

        Movie(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    @SuppressWarnings("unused")
    private static class Catalog {
        private final int id;
        private final List<String> tags;
        private final Set<Integer> codes;
        private final Map<String, Integer> ratings;

        Catalog(int id) {
            this.id = id;
            this.tags = Arrays.asList("tag" + id, "tag" + (id % 97));
            this.codes = new HashSet<>(Arrays.asList(id, id % 89));
            this.ratings = new HashMap<>();
            this.ratings.put("rating" + id, id % 5);
        }
    }

    @Test
    public void budgetedCompactionFocusesOnASingleType() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        producer.runCycle(state -> {
            for (int i = 0; i < 10000; i++) {
                state.add(i);
                state.add("v" + i);
            }
        });

        long v2 = producer.runCycle(state -> {
            for (int i = 10000; i < 20000; i++) {
                state.add(i);
                state.add("v" + i);
            }
        });

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore).build();
        consumer.triggerRefreshTo(v2);
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "Integer"));
        Assert.assertEquals(20000, populatedOrdinalsLength(consumer, "String"));

        /// both types are candidates, but the budget is spent entirely on the one wasting the most space
        long v3 = producer.runCompactionCycle(new CompactionConfig(0, 0, 8192));
        consumer.triggerRefreshTo(v3);

        int integerLength = populatedOrdinalsLength(consumer, "Integer");
        int stringLength = populatedOrdinalsLength(consumer, "String");

        Assert.assertTrue("one type should have been left untouched",
                integerLength == 20000 || stringLength == 20000);
        Assert.assertTrue("the other should have made progress",
                integerLength < 20000 || stringLength < 20000);

        /// both types were eligible all along -- an unbudgeted cycle reclaims the rest of the holes in each at once
        long v4 = producer.runCompactionCycle(new CompactionConfig(0, 0));
        consumer.triggerRefreshTo(v4);
        Assert.assertEquals(10000, populatedOrdinalsLength(consumer, "Integer"));
        Assert.assertEquals(10000, populatedOrdinalsLength(consumer, "String"));
    }

    private static int populatedOrdinalsLength(HollowConsumer consumer, String type) {
        return consumer.getStateEngine().getTypeState(type).getPopulatedOrdinals().length();
    }

    @Test
    public void consumerFilteringSupport() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();

        /// Showing verbose version of `runCycle(producer, 1);`
        long version = producer.runCycle(state -> state.add(1));

        TypeFilter filterConfig = TypeFilter.newTypeFilter()
                .excludeAll()
                .include("String")
                .build();

        HollowConsumer consumer = HollowConsumer.withBlobRetriever(blobStore)
                .withTypeFilter(filterConfig)
                .build();
        consumer.triggerRefreshTo(version);
        Assert.assertEquals(version, consumer.getCurrentVersionId());

        // Filtering is not supported in shared memory mode
        try {
            HollowConsumer.withBlobRetriever(blobStore)
                    .withMemoryMode(MemoryMode.SHARED_MEMORY_LAZY)
                    .withTypeFilter(filterConfig)
                    .build();
        } catch (UnsupportedOperationException e) {
            return;
        }
        Assert.fail();  // fail if UnsupportedOperationException was not thrown
    }

    private long runCycle(HollowProducer producer, final int cycleNumber) {
        return producer.runCycle(state -> state.add(cycleNumber));
    }
}
