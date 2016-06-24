package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;

public class SnapshotSizeCircuitBreaker extends HollowCircuitBreaker {

    private final long snapshotSize;

    public SnapshotSizeCircuitBreaker(PublishWorkflowContext ctx, long versionId, long snapshotSize) {
        super(ctx, versionId);
        this.snapshotSize = snapshotSize;
    }

    @Override
    public String getRuleName() {
        return "HollowBlobSnapshotSize";
    }

    @Override
    protected CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        logSizeToAtlas();
        return compareMetric(snapshotSize);
    }


    private void logSizeToAtlas() {
    	ctx.getMetricRecorder().recordMetric(Metric.SnapShotSize, snapshotSize);
    }

}
