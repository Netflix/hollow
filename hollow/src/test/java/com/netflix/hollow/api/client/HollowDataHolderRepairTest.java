package com.netflix.hollow.api.client;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.HollowConsumer.TransitionAwareRefreshListener;
import com.netflix.hollow.api.custom.HollowAPI;
import com.netflix.hollow.api.metrics.HollowConsumerMetrics;
import com.netflix.hollow.core.memory.MemoryMode;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.write.HollowBlobWriter;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class HollowDataHolderRepairTest {

    private HollowDataHolder dataHolder;
    private HollowRepairApplier repairApplier;
    private HollowReadStateEngine stateEngine;
    private HollowAPIFactory apiFactory;
    private TransitionAwareRefreshListener listener;
    private HollowConsumerMetrics metrics;

    @Before
    public void setUp() {
        stateEngine = new HollowReadStateEngine();
        apiFactory = mock(HollowAPIFactory.class);
        repairApplier = mock(HollowRepairApplier.class);
        listener = mock(TransitionAwareRefreshListener.class);
        metrics = mock(HollowConsumerMetrics.class);

        // Create mock API for factory
        HollowAPI mockAPI = mock(HollowAPI.class);
        when(apiFactory.createAPI(any())).thenReturn(mockAPI);

        dataHolder = new HollowDataHolder(
            stateEngine,
            apiFactory,
            MemoryMode.ON_HEAP,
            mock(HollowConsumer.DoubleSnapshotConfig.class),
            new FailedTransitionTracker(),
            mock(StaleHollowReferenceDetector.class),
            mock(HollowConsumer.ObjectLongevityConfig.class),
            repairApplier,
            metrics
        );
    }

    @Test
    public void testApplyRepairTransition() throws Exception {
        // Arrange
        HollowConsumer.Blob repairBlob = createMockRepairBlob(100L);

        HollowRepairApplier.RepairResult result =
            new HollowRepairApplier.RepairResult(true, 5, new HashMap<>());
        when(repairApplier.repair(any(), any())).thenReturn(result);

        // Act
        dataHolder.applyRepairTransition(repairBlob, new HollowConsumer.RefreshListener[]{listener});

        // Assert
        verify(repairApplier, times(1)).repair(any(), any());
    }

    @Test
    public void testApplyRepairTransitionInvokesListener() throws Exception {
        // Arrange
        HollowConsumer.Blob repairBlob = createMockRepairBlob(100L);

        HollowRepairApplier.RepairResult result =
            new HollowRepairApplier.RepairResult(true, 5, new HashMap<>());
        when(repairApplier.repair(any(), any())).thenReturn(result);

        // Act
        dataHolder.applyRepairTransition(repairBlob, new HollowConsumer.RefreshListener[]{listener});

        // Assert
        ArgumentCaptor<HollowAPI> apiCaptor = ArgumentCaptor.forClass(HollowAPI.class);
        ArgumentCaptor<HollowReadStateEngine> stateCaptor = ArgumentCaptor.forClass(HollowReadStateEngine.class);
        ArgumentCaptor<Long> versionCaptor = ArgumentCaptor.forClass(Long.class);

        verify(listener, times(1)).repairApplied(
            apiCaptor.capture(),
            stateCaptor.capture(),
            versionCaptor.capture()
        );

        assertEquals(100L, versionCaptor.getValue().longValue());
    }

    @Test
    public void testApplyRepairTransitionEmitsMetrics() throws Exception {
        // Arrange
        HollowConsumer.Blob repairBlob = createMockRepairBlob(100L);

        HashMap<String, Integer> repairedByType = new HashMap<>();
        repairedByType.put("TypeA", 3);
        repairedByType.put("TypeB", 2);
        HollowRepairApplier.RepairResult result =
            new HollowRepairApplier.RepairResult(true, 5, repairedByType);
        when(repairApplier.repair(any(), any())).thenReturn(result);

        // Act
        dataHolder.applyRepairTransition(repairBlob, new HollowConsumer.RefreshListener[]{listener});

        // Assert
        verify(metrics, times(1)).recordRepairTriggered(100L);
        verify(metrics, times(1)).recordRepairDuration(eq(100L), anyLong());
        verify(metrics, times(1)).recordRepairOrdinals("TypeA", 3);
        verify(metrics, times(1)).recordRepairOrdinals("TypeB", 2);
    }

    @Test(expected = RuntimeException.class)
    public void testApplyRepairTransitionThrowsWhenRepairFails() throws Exception {
        // Arrange
        HollowConsumer.Blob repairBlob = createMockRepairBlob(100L);

        HollowRepairApplier.RepairResult result =
            new HollowRepairApplier.RepairResult(false, 0, new HashMap<>());
        when(repairApplier.repair(any(), any())).thenReturn(result);

        // Act & Assert
        dataHolder.applyRepairTransition(repairBlob, new HollowConsumer.RefreshListener[]{listener});
    }

    @Test(expected = IllegalStateException.class)
    public void testApplyRepairTransitionThrowsWhenApplierNull() throws Exception {
        // Arrange
        HollowDataHolder dataHolderWithoutApplier = new HollowDataHolder(
            stateEngine,
            apiFactory,
            MemoryMode.ON_HEAP,
            mock(HollowConsumer.DoubleSnapshotConfig.class),
            new FailedTransitionTracker(),
            mock(StaleHollowReferenceDetector.class),
            mock(HollowConsumer.ObjectLongevityConfig.class),
            null,  // No repair applier
            metrics
        );

        HollowConsumer.Blob repairBlob = createMockRepairBlob(100L);

        // Act & Assert
        dataHolderWithoutApplier.applyRepairTransition(repairBlob, new HollowConsumer.RefreshListener[]{listener});
    }

    @Test
    public void testApplyRepairTransitionWithSchemaChange() throws Exception {
        HollowWriteStateEngine writeEngineV1 = new HollowWriteStateEngine();
        com.netflix.hollow.core.write.objectmapper.HollowObjectMapper mapperV1 =
            new com.netflix.hollow.core.write.objectmapper.HollowObjectMapper(writeEngineV1);
        mapperV1.add(new Movie(1, "Inception"));
        writeEngineV1.prepareForWrite();

        ByteArrayOutputStream v1Baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngineV1).writeSnapshot(v1Baos);
        byte[] v1Snapshot = v1Baos.toByteArray();

        HollowReadStateEngine consumerState = new HollowReadStateEngine();
        new com.netflix.hollow.core.read.engine.HollowBlobReader(consumerState)
            .readSnapshot(new ByteArrayInputStream(v1Snapshot));

        HollowWriteStateEngine writeEngineV2 = new HollowWriteStateEngine();
        com.netflix.hollow.core.write.objectmapper.HollowObjectMapper mapperV2 =
            new com.netflix.hollow.core.write.objectmapper.HollowObjectMapper(writeEngineV2);
        mapperV2.add(new Movie(1, "Inception"));
        mapperV2.add(new Actor(1, "Leonardo DiCaprio"));
        writeEngineV2.prepareForWrite();

        ByteArrayOutputStream v2Baos = new ByteArrayOutputStream();
        new HollowBlobWriter(writeEngineV2).writeSnapshot(v2Baos);
        byte[] v2Snapshot = v2Baos.toByteArray();

        HollowConsumer.Blob repairBlob = mock(HollowConsumer.Blob.class);
        when(repairBlob.getBlobType()).thenReturn(HollowConsumer.Blob.BlobType.REPAIR);
        when(repairBlob.getToVersion()).thenReturn(2L);
        when(repairBlob.getFromVersion()).thenReturn(2L);
        when(repairBlob.getInputStream()).thenAnswer(inv -> new ByteArrayInputStream(v2Snapshot));

        HollowDataHolder holder = new HollowDataHolder(
            consumerState,
            apiFactory,
            MemoryMode.ON_HEAP,
            mock(HollowConsumer.DoubleSnapshotConfig.class),
            new FailedTransitionTracker(),
            mock(StaleHollowReferenceDetector.class),
            mock(HollowConsumer.ObjectLongevityConfig.class),
            new HollowRepairApplier(),
            metrics
        );

        assertNull("Consumer should not have Actor before repair", consumerState.getTypeState("Actor"));

        holder.applyRepairTransition(repairBlob, new HollowConsumer.RefreshListener[]{listener});

        assertNotNull("Consumer should have Actor after repair", consumerState.getTypeState("Actor"));
        assertNotNull("Consumer should have Movie after repair", consumerState.getTypeState("Movie"));
    }

    static class Movie {
        int id;
        String title;
        Movie(int id, String title) {
            this.id = id;
            this.title = title;
        }
    }

    static class Actor {
        int id;
        String name;
        Actor(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private HollowConsumer.Blob createMockRepairBlob(long version) throws Exception {
        HollowConsumer.Blob blob = mock(HollowConsumer.Blob.class);
        when(blob.getBlobType()).thenReturn(HollowConsumer.Blob.BlobType.REPAIR);
        when(blob.getToVersion()).thenReturn(version);
        when(blob.getFromVersion()).thenReturn(version);

        // Create a valid minimal snapshot blob with header
        // Hollow snapshot format requires at least a header
        byte[] minimalSnapshot = createMinimalSnapshotBlob();
        when(blob.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(minimalSnapshot));

        return blob;
    }

    private byte[] createMinimalSnapshotBlob() {
        // Create a minimal valid Hollow snapshot blob using HollowWriteStateEngine
        HollowWriteStateEngine writeEngine = new HollowWriteStateEngine();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            HollowBlobWriter writer = new HollowBlobWriter(writeEngine);
            writer.writeSnapshot(baos);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return baos.toByteArray();
    }
}
