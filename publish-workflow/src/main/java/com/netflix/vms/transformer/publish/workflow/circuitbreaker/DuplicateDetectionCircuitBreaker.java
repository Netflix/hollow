package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.publish.workflow.IndexDuplicateChecker;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class DuplicateDetectionCircuitBreaker extends HollowCircuitBreaker {

    public DuplicateDetectionCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
        super(ctx, vip, versionId);
    }

    @Override
    public String getRuleName() {
        return "DuplicateDetection";
    }

    @Override
    public CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        IndexDuplicateChecker dupChecker = new IndexDuplicateChecker(stateEngine);
        dupChecker.checkDuplicates();

        // Spot to trigger Cycle Monkey if enabled
        ctx.getCycleMonkey().doMonkeyBusiness("DuplicateDetectionCircuitBreaker");

        if (!dupChecker.wasDupKeysDetected())
            return PASSED;

        CircuitBreakerResults results = new CircuitBreakerResults();
        for (Map.Entry<String, Collection<Object[]>> dupEntry : dupChecker.getResults().entrySet()) {
            StringBuilder message = new StringBuilder("Duplicate keys found for type ");
            message.append(dupEntry.getKey()).append(": ");

            for(Object[] key : dupEntry.getValue()) {
                message.append(Arrays.toString(key)).append(" ");
            }

            results.addResult(false, message.toString());
        }

        return results;
    }
}
