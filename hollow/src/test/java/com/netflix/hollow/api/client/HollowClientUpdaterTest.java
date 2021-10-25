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
package com.netflix.hollow.api.client;

import static com.netflix.hollow.core.HollowConstants.VERSION_LATEST;
import static com.netflix.hollow.core.HollowConstants.VERSION_NONE;
import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HollowClientUpdaterTest {
    @Mock private HollowConsumer.BlobRetriever retriever;
    @Mock private HollowConsumer.DoubleSnapshotConfig snapshotConfig;
    @Mock private HollowConsumerMetrics metrics;

    @Mock private HollowConsumer.ObjectLongevityConfig objectLongevityConfig;
    @Mock private HollowConsumer.ObjectLongevityDetector objectLongevityDetector;
    @Mock private HollowAPIFactory apiFactory;

    private HollowClientUpdater subject;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        MemoryMode memoryMode = MemoryMode.ON_HEAP;

        subject = new HollowClientUpdater(retriever, emptyList(), apiFactory, snapshotConfig,
                null, memoryMode, objectLongevityConfig, objectLongevityDetector, metrics, null);
    }

    @Test
    public void testUpdateTo_noVersions() throws Throwable {
        when(snapshotConfig.allowDoubleSnapshot()).thenReturn(false);

        assertTrue(subject.updateTo(VERSION_NONE));
        HollowReadStateEngine readStateEngine = subject.getStateEngine();
        assertTrue("Should have no types", readStateEngine.getAllTypes().isEmpty());
        assertTrue("Should create snapshot plan next, even if double snapshot config disallows it",
                subject.shouldCreateSnapshotPlan());
        assertTrue(subject.updateTo(VERSION_NONE));
        assertTrue("Should still have no types", readStateEngine.getAllTypes().isEmpty());
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testUpdateTo_updateToLatestButNoVersionsRetrieved_throwsException() throws Throwable {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Could not create an update plan, because no existing versions could be retrieved.");
        subject.updateTo(VERSION_LATEST);
    }

    @Test
    public void testUpdateTo_updateToArbitraryVersionButNoVersionsRetrieved_throwsException() throws Throwable {
        long v = Long.MAX_VALUE - 1;
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Could not create an update plan for version %s, because that "
                + "version or any qualifying previous versions could not be retrieved.", v));
        subject.updateTo(v);
    }

    @Test
    public void initialLoad_beforeFirst() {
        CompletableFuture<Long> initialLoad = subject.getInitialLoad();

        assertFalse(initialLoad.isDone());
        assertFalse(initialLoad.isCancelled());
        assertFalse(initialLoad.isCompletedExceptionally());
    }

    @Test
    public void initialLoad_firstFailed() {
        when(retriever.retrieveSnapshotBlob(anyLong()))
                .thenThrow(new RuntimeException("boom"));

        try {
            subject.updateTo(VERSION_LATEST);
            fail("should throw");
        } catch (Throwable th) {
            assertEquals("boom", th.getMessage());
        }

        assertFalse(subject.getInitialLoad().isCompletedExceptionally());
    }

    @Test
    public void testInitialLoadSucceedsThenBadUpdatePlan_throwsException() throws Throwable {
        // much setup
        // 1. construct a real-ish snapshot blob
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngineBuilder()
                .add("hello")
                .build();
        // TODO(timt): DRY with TestHollowConsumer::addSnapshot
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new HollowBlobWriter(stateEngine).writeSnapshot(os);
        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        // 2. fake a snapshot blob
        HollowConsumer.Blob blob = mock(HollowConsumer.Blob.class);
        when(blob.isSnapshot())
                .thenReturn(true);
        when(blob.getInputStream())
                .thenReturn(is);
        // 3. return fake snapshot when asked
        when(retriever.retrieveSnapshotBlob(anyLong()))
                .thenReturn(blob);

        // such act
        subject.updateTo(VERSION_LATEST);

        // amaze!
        assertTrue(subject.getInitialLoad().isDone());

        // test exception msg when subsequent update fails to fetch qualifying versions
        long v = Long.MAX_VALUE - 1;
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(String.format("Could not create an update plan for version %s, because that "
                + "version or any qualifying previous versions could not be retrieved. Consumer will remain at current "
                + "version %s until next update attempt.", v, subject.getCurrentVersionId()));
        subject.updateTo(v);
    }

    @Test
    public void doubleSnapshotIfSchemaChangeSignaled() throws Exception {
        HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig = new HollowConsumer.DoubleSnapshotConfig() {
            @Override public boolean allowDoubleSnapshot() {
                return true;
            }
            @Override public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }
            @Override public boolean attemptOnSchemaChange() {
                return true;
            }
        };
        TransitionListener transitionListener = transitionAConsumerAndSignalSchemaChange(doubleSnapshotConfig, true);
        // Double snapshot transition will be applied because we signal schema change and DoubleSnapshotConfig allows
        // double snapshot on schema change.
        assertTrue(transitionListener.getSnapshot());
    }

    @Test
    public void noDoubleSnapshotOnSchemaChangeIfNotConfigured() throws IOException {
        HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig = new HollowConsumer.DoubleSnapshotConfig() {
            @Override public boolean allowDoubleSnapshot() {
                return true;
            }
            @Override public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }
            @Override public boolean attemptOnSchemaChange() {
                return false;
            }
        };
        TransitionListener transitionListener = transitionAConsumerAndSignalSchemaChange(doubleSnapshotConfig, true);
        // Delta transition will be applied despite signaling schema change because DoubleSnapshotConfig does not
        // attempt double snapshot on schema change
        assertTrue(transitionListener.getDelta());
    }

    @Test
    public void noDoubleSnapshotOnSchemaChangeIfDoubleSnapshotsDisabled() throws IOException {
        HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig = new HollowConsumer.DoubleSnapshotConfig() {
            @Override public boolean allowDoubleSnapshot() {
                return false;
            }
            @Override public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }
            @Override public boolean attemptOnSchemaChange() {
                return true;
            }
        };
        TransitionListener transitionListener = transitionAConsumerAndSignalSchemaChange(doubleSnapshotConfig, true);
        // Delta transition will be applied despite signaling schema change because DoubleSnapshotConfig does not
        // allow double snapshots
        assertTrue(transitionListener.getDelta());
    }

    @Test
    public void noDoubleSnapshotIfNoSchemaChangeSignaled() throws Exception {
        HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig = new HollowConsumer.DoubleSnapshotConfig() {
            @Override public boolean allowDoubleSnapshot() {
                return true;
            }
            @Override public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }
            @Override public boolean attemptOnSchemaChange() {
                return true;
            }
        };
        TransitionListener transitionListener = transitionAConsumerAndSignalSchemaChange(doubleSnapshotConfig, false);
        // Delta transition was applied because although DoubleSnapshotConfig was configured to double snapshot on
        // schema change, no signal was sent to consumer that schema changed
        assertTrue(transitionListener.getDelta());
        assertFalse(transitionListener.getSnapshot());
    }

    /**
     * Transitions a TestHollowConsumer to initial load and then applies a refresh when both snapshot and
     * delta are available. It is used to test which transition was applied by the consumer for variations
     * of {@code DoubleSnapshotConfig} and {@code signalSchemaChange}.
     */
    private TransitionListener transitionAConsumerAndSignalSchemaChange(
            HollowConsumer.DoubleSnapshotConfig doubleSnapshotConfig, boolean signalSchemaChange)
            throws IOException {
        TransitionListener transitionListener = new TransitionListener();
        TestHollowConsumer consumer = new TestHollowConsumer.Builder()
                .withBlobRetriever(new TestBlobRetriever())
                .withDoubleSnapshotConfig(doubleSnapshotConfig)
                .withRefreshListener(transitionListener)
                .build();

        HollowWriteStateEngine state1 = new HollowWriteStateEngineBuilder(Collections.singletonList(Object.class))
                .add(new Object())
                .build();
        HollowWriteStateEngine state2Snapshot = new HollowWriteStateEngineBuilder(Collections.singletonList(
                Object.class))
                .add(new Object())
                .build();
        HollowWriteStateEngine state2Delta = new HollowWriteStateEngineBuilder(Collections.singletonList(Object.class))
                .add(new Object())
                .build();

        // SNAPSHOT
        consumer.applySnapshot(1L, state1);
        assertTrue(transitionListener.getSnapshot());
        assertFalse(transitionListener.getDelta());

        // Register both SNAPSHOT and DELTA, let HollowClientUpdater pick which to apply based on
        // DoubleSnapshotConfig. Later, identify which one was applied based on data in snapshot or delta state
        consumer.addSnapshot(2L, state2Snapshot);
        consumer.addDelta(1L, 2L, state2Delta);

        if (signalSchemaChange) {
            consumer.schemaChanged();   // signal to consumer that schema changed
        }

        consumer.triggerRefreshTo(2L);
        return transitionListener;
    }

    /**
     * Detects whether a consumer transition involved a snapshot or delta update
     */
    class TransitionListener implements HollowConsumer.RefreshListener {
        private boolean snapshot = false;
        private boolean delta = false;

        boolean getSnapshot() {
            return snapshot;
        }
        boolean getDelta() {
            return delta;
        }

        @Override public void refreshStarted(long currentVersion, long requestedVersion) {
            snapshot = false;
            delta = false;
        }
        @Override public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            snapshot = true;
        }
        @Override public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
            delta = true;
        }
        @Override public void refreshFailed(long beforeVersion, long afterVersion, long requestedVersion, Throwable failureCause) {
            fail();
        }

        @Override public void blobLoaded(HollowConsumer.Blob transition) {}
        @Override public void refreshSuccessful(long beforeVersion, long afterVersion, long requestedVersion) {}
    }
}
