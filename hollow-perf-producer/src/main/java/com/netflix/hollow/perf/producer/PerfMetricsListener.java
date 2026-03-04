package com.netflix.hollow.perf.producer;

import com.netflix.hollow.api.producer.HollowProducer;
import com.netflix.hollow.api.producer.PublishStageStats;
import com.netflix.hollow.api.producer.Status;
import com.netflix.hollow.api.producer.listener.CycleListener;
import com.netflix.hollow.api.producer.listener.PopulateListener;
import com.netflix.hollow.api.producer.listener.PublishListener;
import com.netflix.hollow.api.producer.listener.RestoreListener;

import java.time.Duration;

public class PerfMetricsListener implements CycleListener, PopulateListener, PublishListener, RestoreListener {

    private PerfMetrics.CycleMetrics currentCycle;
    private PerfMetrics.RestoreMetrics restoreMetrics;

    // --- CycleListener ---

    @Override
    public void onCycleSkip(CycleSkipReason reason) {
    }

    @Override
    public void onNewDeltaChain(long version) {
    }

    @Override
    public void onCycleStart(long version) {
        currentCycle = new PerfMetrics.CycleMetrics();
        currentCycle.version = version;
        currentCycle.heapUsedBytes = usedHeap();
    }

    @Override
    public void onCycleComplete(Status status, HollowProducer.ReadState readState, long version, Duration elapsed) {
        if (currentCycle != null) {
            currentCycle.cycleDurationMs = elapsed.toMillis();
            currentCycle.heapUsedBytes = usedHeap();
        }
    }

    // --- PopulateListener ---

    @Override
    public void onPopulateStart(long version) {
    }

    @Override
    public void onPopulateComplete(Status status, long version, Duration elapsed) {
        if (currentCycle != null) {
            currentCycle.populateDurationMs = elapsed.toMillis();
        }
    }

    // --- PublishListener ---

    @Override
    public void onNoDeltaAvailable(long version) {
    }

    @Override
    public void onPublishStart(long version) {
    }

    @Override
    public void onBlobStage(Status status, HollowProducer.Blob blob, Duration elapsed) {
        if (currentCycle != null) {
            currentCycle.blobStagingMs.put(blob.getType().name(), elapsed.toMillis());
        }
    }

    @Override
    public void onBlobPublish(Status status, HollowProducer.Blob blob, Duration elapsed) {
        if (currentCycle != null) {
            currentCycle.blobPublishMs.put(blob.getType().name(), elapsed.toMillis());
        }
    }

    @Override
    public void onPublishComplete(Status status, long version, Duration elapsed) {
    }

    @Override
    public void onPublishComplete(Status status, long version, Duration elapsed, PublishStageStats stats) {
        if (currentCycle != null) {
            currentCycle.publishDurationMs = elapsed.toMillis();
        }
    }

    // --- RestoreListener ---

    @Override
    public void onProducerRestoreStart(long restoreVersion) {
        restoreMetrics = new PerfMetrics.RestoreMetrics();
    }

    @Override
    public void onProducerRestoreComplete(Status status, long versionDesired, long versionReached, Duration elapsed) {
        if (restoreMetrics != null) {
            restoreMetrics.restoreDurationMs = elapsed.toMillis();
            restoreMetrics.restoredVersion = versionReached;
        }
    }

    // --- Accessors ---

    public PerfMetrics.CycleMetrics getCurrentCycle() {
        return currentCycle;
    }

    public PerfMetrics.RestoreMetrics getRestoreMetrics() {
        return restoreMetrics;
    }

    private static long usedHeap() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }
}
