package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.servo.monitor.DynamicGauge;
import com.netflix.servo.tag.BasicTag;
import com.netflix.servo.tag.BasicTagList;
import com.netflix.servo.tag.Tag;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import java.util.Arrays;

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
        /// TODO: Not really the right place for this, in the circuit breaker.
        logSizeToAtlas();

        return compareMetric(snapshotSize);
    }


    private void logSizeToAtlas() {
        BasicTagList tagList = new BasicTagList(
                Arrays.<Tag> asList(new BasicTag("vip", ctx.getVip())));
        DynamicGauge.set("vms.hollow.snapshotSize", tagList, Double.valueOf(snapshotSize));
    }

}
