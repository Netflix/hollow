package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.publish.workflow.IndexDuplicateChecker;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;

public class DuplicateDetectionCircuitBreaker extends HollowCircuitBreaker {

    public DuplicateDetectionCircuitBreaker(PublishWorkflowContext ctx, long versionId) {
        super(ctx, versionId);
    }

    @Override
    public String getRuleName() {
        return "DuplicateDetection";
    }

    @Override
    public CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        IndexDuplicateChecker dupChecker = new IndexDuplicateChecker(stateEngine);
        dupChecker.checkDuplicates();

        if (!dupChecker.wasDupKeysDetected())
            return PASSED;

        CircuitBreakerResults results = new CircuitBreakerResults();
        for (String type : dupChecker.getResults()) {
            results.addResult(false, "Duplicate keys found for type: " + type);
        }

        return results;
    }
}
