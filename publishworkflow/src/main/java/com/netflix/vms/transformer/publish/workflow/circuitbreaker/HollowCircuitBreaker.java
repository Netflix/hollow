package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import static com.netflix.vms.transformer.common.TransformerLogger.LogTag.CircuitBreaker;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.hollow.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HollowCircuitBreaker {


    public static final CircuitBreakerResults PASSED = new CircuitBreakerResults(true, "");

    protected final PublishWorkflowContext ctx;
    protected final long versionId;

    private final Map<String, Long> successCountsForCycle;

    public HollowCircuitBreaker(PublishWorkflowContext ctx, long versionId) {
        this.ctx = ctx;
        this.successCountsForCycle = new HashMap<String, Long>();
        this.versionId = versionId;
    }

    public abstract String getRuleName();
    
    public boolean isCountrySpecific() {
    	return false;
    }

    public CircuitBreakerResults run(HollowReadStateEngine stateEngine) {
        if(!ctx.getConfig().isCircuitBreakerEnabled(getRuleName())) {
            ctx.getLogger().warn(CircuitBreaker, "Circuit breaker rule: " + getRuleName() + " is disabled!");
            return PASSED;
        }

        CircuitBreakerResults results = runCircuitBreaker(stateEngine);

        return results;
    }

    protected abstract CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine);

    protected CircuitBreakerResults compareMetric(long currentValue) {
        double threshold = ctx.getConfig().getCircuitBreakerThreshold(getRuleName());
        double changeThresholdPercent = threshold / 100;
        return compareMetric(getRuleName(), currentValue, changeThresholdPercent);
    }

    protected CircuitBreakerResults compareMetric(String metricName, long currentValue, double changeThresholdPercent) {

        successCountsForCycle.put(metricName, currentValue);

        try {
            long expectedValue = getExpectedCount(metricName);
            if((double)Math.abs(expectedValue - currentValue) > (double)(expectedValue * changeThresholdPercent)){
                return new CircuitBreakerResults(false, "Hollow validation failure for " + metricName + ": "
                        + "This will result failure of publish and announce of data."
                        + "Expected value: " + expectedValue + "; Observed value: " + currentValue + "; Threshold: " + changeThresholdPercent);
            }

            return new CircuitBreakerResults(true, "Metric \"" + metricName + "\" current value: " + currentValue + " expected value: " + expectedValue + " threshold: " + changeThresholdPercent);
        } catch(Exception e) {
            ctx.getLogger().info(CircuitBreaker, "Metric \"" + metricName + "\" current value: " + currentValue);
            if(failedBecauseDataNotYetPopulated(e)) {
                return new CircuitBreakerResults(true, "Hollow validation infrastructure error: I failed to grab the expected count for " +
                            metricName + ", but I believe the failure was due to no previous cycle on this object/vip combo.  Proceeding.");
            } else {
                return new CircuitBreakerResults(false, "Hollow ValidationFailed");
            }
        }
    }

    protected boolean metricExists(String metricName) {
        try {
            ctx.getValidationStatsCassandraHelper().getVipKeyValuePair(ctx.getVip(), metricName);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public void saveSuccessSizesForCycle(long cycleVersion) {
        for(String key: successCountsForCycle.keySet()){
            try {
                ctx.getValidationStatsCassandraHelper().addVipKeyValuePair(ctx.getVip(), key, String.valueOf(successCountsForCycle.get(key)));
            } catch (ConnectionException e) {
                e.printStackTrace();
                ctx.getLogger().warn(CircuitBreaker, "Hollow validation infrastructure error:  Could not write data to C* for " + ctx.getVip() + " vip, " + key + " key");
            }
        }
    }

    private long getExpectedCount(String objectName) throws NumberFormatException, ConnectionException {
        return Long.parseLong(ctx.getValidationStatsCassandraHelper().getVipKeyValuePair(ctx.getVip(), objectName));
    }

    private boolean failedBecauseDataNotYetPopulated(Exception e) {
        return (e instanceof NotFoundException || (e.getCause() != null && e.getCause() instanceof NotFoundException));
    }

    public static class CircuitBreakerResults implements Iterable<CircuitBreakerResult> {
        private final List<CircuitBreakerResult> results;

        public CircuitBreakerResults() {
            this.results = new ArrayList<CircuitBreakerResult>();
        }

        public CircuitBreakerResults(boolean passed, String message) {
            this.results = new ArrayList<CircuitBreakerResult>();
            this.results.add(new CircuitBreakerResult(passed, message));
        }

        public void addResult(boolean passed, String message) {
            results.add(new CircuitBreakerResult(passed, message));
        }

        public void addResult(CircuitBreakerResults results) {
            this.results.addAll(results.results);
        }

        @Override
        public Iterator<CircuitBreakerResult> iterator() {
            return results.iterator();
        }
    }


    public static class CircuitBreakerResult {
        private final boolean passed;
        private final String message;

        public CircuitBreakerResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }

        public boolean isPassed() {
            return passed;
        }

        public String getMessage() {
            return message;
        }

    }

}
