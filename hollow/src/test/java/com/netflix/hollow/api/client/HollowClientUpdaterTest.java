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
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.test.HollowWriteStateEngineBuilder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.concurrent.CompletableFuture;
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

        subject = new HollowClientUpdater(retriever, emptyList(), apiFactory, snapshotConfig,
                null, objectLongevityConfig, objectLongevityDetector, metrics, null);
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
        expectedException.expectMessage(String.format("Could not create an update plan for version %s, because that version or any previous versions could not be retrieved.", v));
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
    public void initialLoad_firstSucceeded() throws Throwable {
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
    }
}
