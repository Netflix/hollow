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
import static com.netflix.hollow.core.HollowStateEngine.HEADER_TAG_SCHEMA_HASH;
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
import com.netflix.hollow.core.HollowStateEngine;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.core.schema.HollowSchemaHash;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowObjectTypeWriteState;
import com.netflix.hollow.core.write.HollowObjectWriteRecord;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import com.netflix.hollow.test.consumer.TestBlob;
import com.netflix.hollow.test.consumer.TestBlobRetriever;
import com.netflix.hollow.test.consumer.TestHollowConsumer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class HollowClientUpdaterTest {
    private HollowConsumer.BlobRetriever retriever;
    private HollowConsumer.DoubleSnapshotConfig snapshotConfig;
    private HollowConsumerMetrics metrics;

    private HollowClientUpdater subject;
    private HollowConsumer.ObjectLongevityConfig objectLongevityConfig;
    private HollowConsumer.ObjectLongevityDetector objectLongevityDetector;
    private HollowAPIFactory apiFactory;

    @Before
    public void setUp() {
        retriever = mock(HollowConsumer.BlobRetriever.class);
        apiFactory = mock(HollowAPIFactory.class);
        snapshotConfig = mock(HollowConsumer.DoubleSnapshotConfig.class);
        objectLongevityConfig = mock(HollowConsumer.ObjectLongevityConfig.class);
        objectLongevityDetector = mock(HollowConsumer.ObjectLongevityDetector.class);
        metrics = mock(HollowConsumerMetrics.class);
        MemoryMode memoryMode = MemoryMode.ON_HEAP;

        subject = new HollowClientUpdater(retriever, emptyList(), apiFactory, snapshotConfig,
                null, memoryMode, objectLongevityConfig, objectLongevityDetector, metrics, null);
    }

    @Test
    public void testUpdateTo_noVersions() throws Throwable {
        when(snapshotConfig.allowDoubleSnapshot()).thenReturn(false);
        when(snapshotConfig.doubleSnapshotOnSchemaChange()).thenReturn(false);

        assertTrue(subject.updateTo(VERSION_NONE));
        HollowReadStateEngine readStateEngine = subject.getStateEngine();
        assertTrue("Should have no types", readStateEngine.getAllTypes().isEmpty());
        assertTrue("Should create snapshot plan next, even if double snapshot config disallows it",
                subject.shouldCreateSnapshotPlan(new HollowConsumer.VersionInfo(VERSION_NONE)));
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
    public void testUpdateTo_pinnedVersion() {
        long pinnedVersion = 1000L;
        HashMap<String, String> annData = new HashMap<>();
        try {
            subject.updateTo(new HollowConsumer.VersionInfo(pinnedVersion, Optional.of(annData), Optional.of(Boolean.TRUE)));
        } catch(Throwable ex) {
            // do nothing
        }
    }

    @Test
    public void testDoubleSnapshotOnSchemaChange() throws Exception {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        TestHollowConsumer testHollowConsumer = schemaChangeSubject(stateEngine, true, false, true, true);
        Map<String, String> v2Metadata = new HashMap<String, String>() {{ put(HollowStateEngine.HEADER_TAG_SCHEMA_HASH, (new HollowSchemaHash(stateEngine)).getHash()); }};
        testHollowConsumer.triggerRefreshTo(new HollowConsumer.VersionInfo(2, Optional.of(v2Metadata), Optional.empty()));
    }

    @Test
    public void testDoubleSnapshotOnSchemaChange_flagDisabled() throws Exception {
        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        TestHollowConsumer testHollowConsumer = schemaChangeSubject(stateEngine, false, true, false, true);
        Map<String, String> v2Metadata = new HashMap<String, String>() {{ put(HollowStateEngine.HEADER_TAG_SCHEMA_HASH, (new HollowSchemaHash(stateEngine)).getHash()); }};
        testHollowConsumer.triggerRefreshTo(new HollowConsumer.VersionInfo(2, Optional.of(v2Metadata), Optional.empty()));
    }

    @Test
    public void testDoubleSnapshotOnSchemaChange_noVersionMetadata_logsWarning() throws Exception {
        WarnLogHandler logHandler = (WarnLogHandler) configureLogger(HollowClientUpdater.class.getName(), Level.WARNING,
                "Double snapshots on schema change are enabled and its functioning depends on " +
                        "visibility into incoming version's schema through metadata but NO metadata was available " +
                        "for version 2. Check that the mechanism that triggered " +
                        "the refresh (usually announcementWatcher) supports passing version metadata. This refresh will " +
                        "not be able to reflect any schema changes.");

        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        TestHollowConsumer testHollowConsumer = schemaChangeSubject(stateEngine, true, true, false, true);
        testHollowConsumer.triggerRefreshTo(2);

        assertTrue("Warning should be logged", logHandler.isContains());
    }

    @Test
    public void testDoubleSnapshotOnSchemaChange_noSchemaHashInMetadata_logsWarning() throws Exception {
        WarnLogHandler logHandler = (WarnLogHandler) configureLogger(HollowClientUpdater.class.getName(), Level.WARNING,
                "Double snapshots on schema change are enabled but version metadata for incoming " +
                        "version 2 did not contain the required attribute (" +
                        HEADER_TAG_SCHEMA_HASH + "). Check that the producer supports setting this attribute. This " +
                        "refresh will not be able to reflect any schema changes.");

        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        TestHollowConsumer testHollowConsumer = schemaChangeSubject(stateEngine, true, true, false, true);
        testHollowConsumer.triggerRefreshTo(new HollowConsumer.VersionInfo(2, Optional.of(new HashMap<>()), Optional.empty()));

        assertTrue("Warning should be logged", logHandler.isContains());
    }

    @Test
    public void testDoubleSnapshotOnSchemaChange_prohibitDoubleSnapshot_logsWarning() throws Exception {
        WarnLogHandler logHandler = (WarnLogHandler) configureLogger(HollowClientUpdater.class.getName(), Level.WARNING,
                "Auto double snapshots on schema changes are enabled but double snapshots on consumer " +
                        "are prohibited by doubleSnapshotConfig. This refresh will not be able to reflect any schema changes.");

        HollowWriteStateEngine stateEngine = new HollowWriteStateEngine();
        TestHollowConsumer testHollowConsumer = schemaChangeSubject(stateEngine, true, true, false, false);
        Map<String, String> v2Metadata = new HashMap<String, String>() {{ put(HollowStateEngine.HEADER_TAG_SCHEMA_HASH, (new HollowSchemaHash(stateEngine)).getHash()); }};
        testHollowConsumer.triggerRefreshTo(new HollowConsumer.VersionInfo(2, Optional.of(v2Metadata), Optional.empty()));

        assertTrue("Warning should be logged", logHandler.isContains());
    }

    private static void addMovie(HollowWriteStateEngine stateEngine, int id) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) stateEngine.getSchema("Movie"));
        rec.setInt("id", id);
        stateEngine.add("Movie", rec);
    }

    private static void addActor(HollowWriteStateEngine stateEngine, int id) {
        HollowObjectWriteRecord rec = new HollowObjectWriteRecord((HollowObjectSchema) stateEngine.getSchema("Actor"));
        rec.setInt("id", id);
        stateEngine.add("Actor", rec);
    }

    private TestHollowConsumer schemaChangeSubject(HollowWriteStateEngine stateEngine, boolean doubleSnapshotOnSchemaChange,
                                                   boolean failIfDoubleSnapshot, boolean failIfDelta, boolean allowDoubleSnapshots) throws Exception {
        HollowConsumer.DoubleSnapshotConfig supportsSchemaChange = new HollowConsumer.DoubleSnapshotConfig() {
            @Override
            public boolean allowDoubleSnapshot() {
                return allowDoubleSnapshots;
            }
            @Override
            public int maxDeltasBeforeDoubleSnapshot() {
                return 32;
            }
            @Override
            public boolean doubleSnapshotOnSchemaChange() {
                return doubleSnapshotOnSchemaChange;
            }
        };
        TestBlobRetriever testBlobRetriever = new TestBlobRetriever();

        HollowObjectSchema movieSchema = new HollowObjectSchema("Movie", 1, "id");
        movieSchema.addField("id", HollowObjectSchema.FieldType.INT);

        stateEngine.addTypeState(new HollowObjectTypeWriteState(movieSchema));

        class AssertNoDeltas extends HollowConsumer.AbstractRefreshListener {
            private boolean initialLoad = true;
            @Override
            public void snapshotUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
                if (!initialLoad && failIfDoubleSnapshot) {
                    fail();
                }
                initialLoad = false;
            }
            @Override
            public void deltaUpdateOccurred(HollowAPI api, HollowReadStateEngine stateEngine, long version) throws Exception {
                if (failIfDelta) {
                    fail();
                }
            }
        }
        // v1
        addMovie(stateEngine, 1);
        stateEngine.prepareForWrite(false);
        ByteArrayOutputStream baos_v1 = new ByteArrayOutputStream();
        HollowBlobWriter writer = new HollowBlobWriter(stateEngine);
        writer.writeSnapshot(baos_v1);
        testBlobRetriever.addSnapshot(1, new TestBlob(1,new ByteArrayInputStream(baos_v1.toByteArray())));
        TestHollowConsumer testHollowConsumer = (new TestHollowConsumer.Builder())
                .withBlobRetriever(testBlobRetriever)
                .withDoubleSnapshotConfig(supportsSchemaChange)
                .withRefreshListener(new AssertNoDeltas())
                .build();

        testHollowConsumer.triggerRefreshTo(1);

        // v2
        stateEngine.prepareForNextCycle();
        HollowObjectSchema actorSchema = new HollowObjectSchema("Actor", 1, "id");
        actorSchema.addField("id", HollowObjectSchema.FieldType.INT);
        stateEngine.addTypeState(new HollowObjectTypeWriteState(actorSchema));

        addActor(stateEngine, 1);
        stateEngine.prepareForWrite(false);
        ByteArrayOutputStream baos_v1_to_v2 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v2_to_v1 = new ByteArrayOutputStream();
        ByteArrayOutputStream baos_v2 = new ByteArrayOutputStream();
        writer.writeSnapshot(baos_v2);
        writer.writeDelta(baos_v1_to_v2);
        writer.writeReverseDelta(baos_v2_to_v1);
        testBlobRetriever.addSnapshot(2, new TestBlob(2,new ByteArrayInputStream(baos_v2.toByteArray())));
        testBlobRetriever.addDelta(1, new TestBlob(1, 2, new ByteArrayInputStream(baos_v1_to_v2.toByteArray())));
        testBlobRetriever.addReverseDelta(2, new TestBlob(2, 1, new ByteArrayInputStream(baos_v2_to_v1.toByteArray())));

        return testHollowConsumer;
    }

    private Handler configureLogger(String classLogger, Level level, String msg) {
        Logger logger = LogManager.getLogManager().getLogger(classLogger);
        Handler logHandler = new WarnLogHandler(msg);
        logHandler.setLevel(Level.ALL);
        logger.setUseParentHandlers(false);
        logger.addHandler(logHandler);
        return logHandler;
    }

    private class WarnLogHandler extends Handler {
        private final String msg;
        private boolean contains = false;

        WarnLogHandler(String msg) {
            this.msg = msg;
        }

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().equals(Level.WARNING)) {
                if (record.getMessage().equals(msg)) {
                    contains = true;
                }
            }
        }
        @Override
        public void flush() {
        }
        @Override
        public void close() throws SecurityException {
        }

        public boolean isContains() {
            return contains;
        }
    }


}
