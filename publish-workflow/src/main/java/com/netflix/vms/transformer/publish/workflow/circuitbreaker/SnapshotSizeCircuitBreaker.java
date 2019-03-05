package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;

public class SnapshotSizeCircuitBreaker extends HollowCircuitBreaker {

    private final long snapshotSize;

    public SnapshotSizeCircuitBreaker(TransformerContext ctx, String vip, long versionId, long snapshotSize) {
        super(ctx, vip, versionId);
        this.snapshotSize = snapshotSize;
    }

    @Override
    public String getRuleName() {
        return "HollowBlobSnapshotSize";
    }

    @Override
    protected CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        // Spot to trigger Cycle Monkey if enabled
        ctx.getCycleMonkey().doMonkeyBusiness("SnapshotSizeCircuitBreaker");

        logSizeToAtlas();
        return compareMetric(snapshotSize);
    }


    private void logSizeToAtlas() {
    	ctx.getMetricRecorder().recordMetric(Metric.SnapShotSize, snapshotSize);
    }

}
