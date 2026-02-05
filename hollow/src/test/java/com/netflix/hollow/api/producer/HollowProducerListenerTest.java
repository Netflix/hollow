package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.producer.enforcer.SingleProducerEnforcer;
import com.netflix.hollow.api.producer.fs.HollowInMemoryBlobStager;
import com.netflix.hollow.api.producer.listener.AnnouncementListener;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.DataModelInitializationListener;
import com.netflix.hollow.api.producer.listener.IntegrityCheckListener;
import com.netflix.hollow.api.producer.listener.PopulateListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;
import com.netflix.hollow.api.producer.listener.VetoableListener;
import com.netflix.hollow.api.producer.validation.ValidationStatus;
import com.netflix.hollow.api.producer.validation.ValidationStatusListener;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import com.netflix.hollow.core.memory.ByteArrayOrdinalMapStats;
import com.netflix.hollow.test.InMemoryBlobStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests to verify that HollowProducerListener objects provided to HollowProducers
 * are invoked at the right times.
 */
public class HollowProducerListenerTest {
    private InMemoryBlobStore blobStore;

    @Before
    public void setUp() {
        blobStore = new InMemoryBlobStore();
    }

    static class BaseListener {
        Map<String, Integer> callCount = new HashMap<>();

        void reportCaller() {
            Throwable t = new Throwable();
            StackTraceElement caller = t.getStackTrace()[1];
            callCount.compute(caller.getMethodName(), (k, v) -> v == null ? 1 : v + 1);
        }
    }

    @Test
    public void testListenerVetoException() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) stateVersion -> { })
                .build();

        class Listener implements CycleListener {
            @Override public void onCycleSkip(CycleSkipReason reason) {
            }

            @Override public void onNewDeltaChain(long version) {
            }

            @Override public void onCycleStart(long version) {
                throw new VetoableListener.ListenerVetoException("VETOED");
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
            }
        }
        Listener l = new Listener();
        producer.addListener(l);

        producer.initializeDataModel(Top.class);

        try {
            producer.runCycle(ws -> ws.add(new Top(1)));
            Assert.fail();
        } catch (VetoableListener.ListenerVetoException e) {
            Assert.assertEquals("VETOED", e.getMessage());
        }
    }

    @Test
    public void testVetoableListener() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) stateVersion -> { })
                .build();

        class Listener implements CycleListener, VetoableListener {
            @Override public void onCycleSkip(CycleSkipReason reason) {
            }

            @Override public void onNewDeltaChain(long version) {
            }

            @Override public void onCycleStart(long version) {
                throw new RuntimeException("VETOED");
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
            }
        }
        Listener l = new Listener();
        producer.addListener(l);

        producer.initializeDataModel(Top.class);

        try {
            producer.runCycle(ws -> ws.add(new Top(1)));
            Assert.fail();
        } catch (RuntimeException e) {
            Assert.assertEquals("VETOED", e.getMessage());
        }
    }

    @Test
    public void testFirstCycle() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withAnnouncer((HollowProducer.Announcer) stateVersion -> { })
                .build();

        class Listeners extends BaseListener implements
                DataModelInitializationListener,
                RestoreListener,
                CycleListener,
                PopulateListener,
                IntegrityCheckListener,
                ValidationStatusListener,
                PublishListener,
                AnnouncementListener {

            @Override public void onProducerInit(Duration elapsed) {
                reportCaller();
            }

            @Override public void onProducerRestoreStart(long restoreVersion) {
                Assert.fail();
            }

            @Override public void onProducerRestoreComplete(
                    Status status, long versionDesired, long versionReached, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }

            @Override public void onCycleSkip(CycleSkipReason reason) {
                Assert.fail();
            }

            long newDeltaChainVersion;

            @Override public void onNewDeltaChain(long version) {
                reportCaller();
                newDeltaChainVersion = version;
            }

            @Override public void onCycleStart(long version) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onCycleStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onIntegrityCheckStart(long version) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onIntegrityCheckComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onIntegrityCheckStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onPopulateStart(long version) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onPopulateComplete(Status status, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onPopulateStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onNoDeltaAvailable(long version) {
                Assert.fail();
            }

            @Override public void onPublishStart(long version) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertEquals(HollowProducer.Blob.Type.SNAPSHOT, blob.getType());
                Assert.assertTrue(callCount.containsKey("onPublishStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
            }

            @Override public void onBlobPublishAsync(
                    CompletableFuture<HollowProducer.Blob> blob) {
                Assert.fail();
            }


            @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertEquals(HollowProducer.Blob.Type.SNAPSHOT, blob.getType());
                Assert.assertTrue(callCount.containsKey("onBlobStage"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
                Assert.fail("onPublishComplete(Status status, long version, Duration elapsed) is deprecated.");
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats publishStageStats) {
                Map<String, ByteArrayOrdinalMapStats> stats = publishStageStats.getOrdinalMapStats();
                for (Map.Entry<String, ByteArrayOrdinalMapStats> stat : stats.entrySet()) {
                    Assert.assertTrue(stat.getValue().getMaxOrdinal() >= 0);
                }

                reportCaller();
                Assert.assertTrue(callCount.containsKey("onBlobPublish"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onValidationStatusStart(long version) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onValidationStatusComplete(
                    ValidationStatus status, long version, Duration elapsed) {
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onValidationStatusStart"));
                Assert.assertTrue(status.passed());
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override public void onAnnouncementStart(long version) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, version);
            }

            @Override
            public void onAnnouncementStart(HollowProducer.ReadState readState) {
                reportCaller();
                Assert.assertEquals(newDeltaChainVersion, readState.getVersion());
                Assert.assertNotNull("Read state engine should not be null.", readState.getStateEngine());
            }

            @Override public void onAnnouncementComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onAnnouncementStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(newDeltaChainVersion, version);
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);
        producer.initializeDataModel(Top.class);

        producer.runCycle(ws -> ws.add(new Top(1)));

        Assert.assertTrue(ls.callCount.entrySet().stream().
                filter(c -> !c.getKey().equals("onAnnouncementStart")).
                allMatch(c -> c.getValue() == 1));
        Assert.assertEquals(2, ls.callCount.get("onAnnouncementStart").intValue());
        Assert.assertEquals(16, ls.callCount.size());

    }


    @Test
    public void testSecondCycleWithChanges() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(Top.class);

        producer.runCycle(ws -> ws.add(new Top(1)));

        class Listeners extends BaseListener implements
                DataModelInitializationListener,
                RestoreListener,
                CycleListener,
                PopulateListener,
                IntegrityCheckListener,
                ValidationStatusListener,
                PublishListener,
                AnnouncementListener {

            @Override public void onProducerInit(Duration elapsed) {
                Assert.fail();
            }

            @Override public void onProducerRestoreStart(long restoreVersion) {
                Assert.fail();
            }

            @Override public void onProducerRestoreComplete(
                    Status status, long versionDesired, long versionReached, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }

            @Override public void onCycleSkip(CycleSkipReason reason) {
                Assert.fail();
            }

            long cycleStartVersion;

            @Override public void onNewDeltaChain(long version) {
                Assert.fail();
            }

            @Override public void onCycleStart(long version) {
                reportCaller();
                cycleStartVersion = version;
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onCycleStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onIntegrityCheckStart(long version) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onIntegrityCheckComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onIntegrityCheckStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onPopulateStart(long version) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onPopulateComplete(Status status, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onPopulateStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onNoDeltaAvailable(long version) {
                Assert.fail();
            }

            @Override public void onPublishStart(long version) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onPublishStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
            }

            @Override public void onBlobPublishAsync(
                    CompletableFuture<HollowProducer.Blob> blob) {
                Assert.fail();
            }

            @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onBlobStage"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
                Assert.fail("onPublishComplete(Status status, long version, Duration elapsed) is deprecated.");
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats publishStageStats) {
                Map<String, ByteArrayOrdinalMapStats> stats = publishStageStats.getOrdinalMapStats();
                for (Map.Entry<String, ByteArrayOrdinalMapStats> stat : stats.entrySet()) {
                    Assert.assertTrue(stat.getValue().getMaxOrdinal() >= 0);
                }

                reportCaller();
                Assert.assertTrue(callCount.containsKey("onBlobPublish"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onValidationStatusStart(long version) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onValidationStatusComplete(
                    ValidationStatus status, long version, Duration elapsed) {
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onValidationStatusStart"));
                Assert.assertTrue(status.passed());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onAnnouncementStart(long version) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override
            public void onAnnouncementStart(HollowProducer.ReadState readState) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, readState.getVersion());
                Assert.assertNotNull("Read state engine should not be null.", readState.getStateEngine());
            }

            @Override public void onAnnouncementComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onAnnouncementStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);

        producer.runCycle(ws -> ws.add(new Top(2)));

        Assert.assertTrue(ls.callCount.entrySet().stream()
                .filter(e -> !e.getKey().equals("onBlobStage"))
                .filter(e -> !e.getKey().equals("onBlobPublish"))
                .map(Map.Entry::getValue)
                .allMatch(c -> c == 1));
        // onBlobStage and onBlobPublish invoked 3 times as for the second cycle, there are 3 blobs to be published:
        // snapshot, delta, and reverse delta.
        Assert.assertEquals(3, ls.callCount.get("onBlobStage").intValue());
        Assert.assertEquals(3, ls.callCount.get("onBlobPublish").intValue());
        Assert.assertEquals(12, ls.callCount.size());
    }

    @Test
    public void testSecondCycleNoChanges() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .build();
        producer.initializeDataModel(Top.class);

        producer.runCycle(ws -> ws.add(new Top(1)));

        class Listeners extends BaseListener implements
                DataModelInitializationListener,
                RestoreListener,
                CycleListener,
                PopulateListener,
                IntegrityCheckListener,
                ValidationStatusListener,
                PublishListener,
                AnnouncementListener {

            @Override public void onProducerInit(Duration elapsed) {
                Assert.fail();
            }

            @Override public void onProducerRestoreStart(long restoreVersion) {
                Assert.fail();
            }

            @Override public void onProducerRestoreComplete(
                    Status status, long versionDesired, long versionReached, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }

            @Override public void onCycleSkip(CycleSkipReason reason) {
                Assert.fail();
            }

            long cycleStartVersion;

            @Override public void onNewDeltaChain(long version) {
                Assert.fail();
            }

            @Override public void onCycleStart(long version) {
                reportCaller();
                cycleStartVersion = version;
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onCycleStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onIntegrityCheckStart(long version) {
                Assert.fail();
            }

            @Override public void onIntegrityCheckComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }

            @Override public void onPopulateStart(long version) {
                reportCaller();
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onPopulateComplete(Status status, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onPopulateStart"));
                Assert.assertEquals(Status.StatusType.SUCCESS, status.getType());
                Assert.assertEquals(cycleStartVersion, version);
            }

            @Override public void onNoDeltaAvailable(long version) {
                reportCaller();
                Assert.assertTrue(callCount.containsKey("onPopulateComplete"));
            }

            @Override public void onPublishStart(long version) {
                Assert.fail();
            }

            @Override public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }

            @Override public void onBlobPublishAsync(
                    CompletableFuture<HollowProducer.Blob> blob) {
                Assert.fail();
            }


            @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
                Assert.fail("onPublishComplete(Status status, long version, Duration elapsed) is deprecated.");
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats publishStageStats) {
                reportCaller();
                Assert.assertNull(publishStageStats);
                Assert.assertEquals(Status.StatusType.FAIL, status.getType());
                Assert.assertTrue(status.getCause() instanceof RejectedExecutionException);
            }

            @Override public void onValidationStatusStart(long version) {
                Assert.fail();
            }

            @Override public void onValidationStatusComplete(
                    ValidationStatus status, long version, Duration elapsed) {
                Assert.fail();
            }

            @Override public void onAnnouncementStart(long version) {
                Assert.fail();
            }

            @Override
            public void onAnnouncementStart(HollowProducer.ReadState readState) {
                Assert.fail();
            }

            @Override public void onAnnouncementComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                if (status.getCause() instanceof AssertionError) {
                    return;
                }
                Assert.fail();
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);

        producer.runCycle(ws -> ws.add(new Top(1)));

        Assert.assertTrue(ls.callCount.values().stream().allMatch(c -> c == 1));
        Assert.assertEquals(5, ls.callCount.size());
    }

    @Test
    public void testCycleSkipWithSingleEnforcer() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withSingleProducerEnforcer(new SingleProducerEnforcer() {
                    @Override public void enable() {
                    }

                    @Override public void disable() {
                    }

                    @Override public boolean isPrimary() {
                        return false;
                    }

                    @Override public void force() {
                    }
                })
                .build();

        class Listeners extends BaseListener implements CycleListener {
            @Override public void onCycleSkip(CycleSkipReason reason) {
                reportCaller();
                Assert.assertEquals(HollowProducerListener.CycleSkipReason.NOT_PRIMARY_PRODUCER, reason);
            }

            @Override public void onNewDeltaChain(long version) {
                Assert.fail();
            }

            @Override public void onCycleStart(long version) {
                Assert.fail();
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                Assert.fail();
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);

        producer.runCycle(ws -> ws.add(new Top(1)));

        Assert.assertEquals(1, ls.callCount.size());
    }

    @Test
    public void testCycleStartEndWithSingleEnforcer() {
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withSingleProducerEnforcer(new SingleProducerEnforcer() {
                    @Override public void enable() {
                    }

                    @Override public void disable() {
                    }

                    @Override public boolean isPrimary() {
                        return true;
                    }

                    @Override public void force() {
                    }
                })
                .build();

        class Listeners extends BaseListener implements CycleListener {
            @Override public void onCycleSkip(CycleSkipReason reason) {
                Assert.fail();
            }

            @Override public void onNewDeltaChain(long version) {
                reportCaller();
            }

            @Override public void onCycleStart(long version) {
                reportCaller();
            }

            @Override public void onCycleComplete(
                    Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
                reportCaller();
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);

        producer.runCycle(ws -> ws.add(new Top(1)));

        Assert.assertEquals(3, ls.callCount.size());
    }

    @Test
    public void testBlobPublishAsync() {
        ExecutorService executor = Executors.newCachedThreadPool();
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withSnapshotPublishExecutor(executor)
                .build();
        producer.initializeDataModel(Top.class);

        producer.runCycle(ws -> ws.add(new Top(1)));

        class Listeners extends BaseListener implements PublishListener {
            CompletableFuture<HollowProducer.Blob> snapshotBlob;

            @Override public void onNoDeltaAvailable(long version) {
            }

            @Override public void onPublishStart(long version) {
            }

            @Override public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
            }


            @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
                Assert.assertNotEquals(HollowProducer.Blob.Type.SNAPSHOT, blob.getType());
            }

            @Override public void onBlobPublishAsync(CompletableFuture<HollowProducer.Blob> blob) {
                reportCaller();
                this.snapshotBlob = blob.thenApply(b -> {
                    Assert.assertEquals(HollowProducer.Blob.Type.SNAPSHOT, b.getType());
                    try {
                        InputStream contents = b.newInputStream();
                        contents.read();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return b;
                });
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);

        producer.runCycle(ws -> ws.add(new Top(2)));

        Assert.assertEquals(1, ls.callCount.size());
        Assert.assertNotNull(ls.snapshotBlob);

        HollowProducer.Blob b = ls.snapshotBlob.join();
    }

    @Test
    public void testBlobPublishAsyncExecutorFail() {
        Executor executor = (r) -> { throw new RejectedExecutionException(); };
        HollowProducer producer = HollowProducer.withPublisher(blobStore)
                .withBlobStager(new HollowInMemoryBlobStager())
                .withSnapshotPublishExecutor(executor)
                .build();
        producer.initializeDataModel(Top.class);

        producer.runCycle(ws -> ws.add(new Top(1)));

        class Listeners extends BaseListener implements PublishListener {
            CompletableFuture<HollowProducer.Blob> snapshotBlob;

            @Override public void onNoDeltaAvailable(long version) {
            }

            @Override public void onPublishStart(long version) {
            }

            @Override public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
            }

            @Override public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
                Assert.assertNotEquals(HollowProducer.Blob.Type.SNAPSHOT, blob.getType());
            }

            @Override public void onBlobPublishAsync(CompletableFuture<HollowProducer.Blob> blob) {
                reportCaller();
                this.snapshotBlob = blob;
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed) {
                reportCaller();
                Assert.assertEquals(Status.StatusType.FAIL, status.getType());
                Assert.assertTrue(status.getCause() instanceof RejectedExecutionException);
            }

            @Override public void onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats publishStageStats) {
                reportCaller();
                Assert.assertNull(publishStageStats);
                Assert.assertEquals(Status.StatusType.FAIL, status.getType());
                Assert.assertTrue(status.getCause() instanceof RejectedExecutionException);
            }
        }

        Listeners ls = new Listeners();
        producer.addListener(ls);

        try {
            producer.runCycle(ws -> ws.add(new Top(2)));
            Assert.fail();
        } catch (RejectedExecutionException e) {
        }

        Assert.assertEquals(2, ls.callCount.size());
        Assert.assertNotNull(ls.snapshotBlob);
        Assert.assertTrue(ls.snapshotBlob.isCompletedExceptionally());

        try {
            ls.snapshotBlob.join();
            Assert.fail();
        } catch (CompletionException e) {
            Assert.assertTrue(e.getCause() instanceof RejectedExecutionException);
        }
    }

    static class Top {
        final int id;

        Top(int id) {
            this.id = id;
        }
    }
}
