package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import static com.netflix.vms.transformer.common.cassandra.TransformerCassandraHelper.TransformerColumnFamily.CIRCUITBREAKER_STATS;
import static com.netflix.vms.transformer.common.io.TransformerLogTag.CircuitBreaker;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.connectionpool.exceptions.NotFoundException;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.cassandra.TransformerCassandraColumnFamilyHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class HollowCircuitBreaker {

    public static final CircuitBreakerResults PASSED = new CircuitBreakerResults(true, "");

    protected final TransformerContext ctx;
    protected final String vip;
    protected final long versionId;
    private final Map<String, Double> successCountsForCycle;

    public HollowCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
        this.ctx = ctx;
        this.vip = vip;
        this.versionId = versionId;
        this.successCountsForCycle = new HashMap<String, Double>();
    }

    public abstract String getRuleName();
    
    public boolean isCountrySpecific() {
    	return false;
    }

    public CircuitBreakerResults run(HollowReadStateEngine stateEngine) {
        if(!ctx.getConfig().isCircuitBreakerEnabled(getRuleName())) {
            ctx.getLogger().warn(CircuitBreaker, "Circuit breaker rule: {} is disabled!", getRuleName());
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

    protected CircuitBreakerResults compareMetric(String metricName, double currentValue, double changeThresholdPercent) {

        successCountsForCycle.put(metricName, currentValue);

        try {
            double baseLine = getBaseLine(metricName);
            if(isCBCheckFailed(currentValue, changeThresholdPercent, baseLine)){
                return new CircuitBreakerResults(false, getFailedCBMessage(metricName, currentValue, changeThresholdPercent, baseLine));
            }

            return new CircuitBreakerResults(true, getSuccessMessage(metricName, currentValue, changeThresholdPercent, baseLine));
        
        } catch(Exception e) {
            ctx.getLogger().info(CircuitBreaker, "Metric \"{}\" current value: {}", metricName, currentValue);
            if(failedBecauseDataNotYetPopulated(e)) {
                return new CircuitBreakerResults(true, "Hollow validation infrastructure error: I failed to grab the expected count for " +
                            metricName + ", but I believe the failure was due to no previous cycle on this object/vip combo.  Proceeding.");
            } else {
                ctx.getLogger().error(CircuitBreaker, "Rule failed with Exception.  Rule: {} metric: {}", getRuleName(), metricName, e);
                return new CircuitBreakerResults(false, "Hollow Validation Failed");
            }
        }
    }

	protected String getSuccessMessage(String metricName, double currentValue, double changeThresholdPercent, double baseLine) {
		return "Metric \"" + metricName + "\" current value: " + currentValue + " expected value: " + baseLine + " threshold: " + changeThresholdPercent;
	}

    protected String getFailedCBMessage(String metricName, double currentValue, double changeThresholdPercent, double baseLine) {
    	String changeType = (currentValue < baseLine) ? "drop" : "addition";
		return "Hollow validation failure for " + metricName + ": "
		        + "This will result failure of announcement of data to clients."
		        + "Observed a change percent of "+getChangePercent(currentValue, baseLine)+" ("+changeType+" from: "+baseLine+" to: +"+currentValue+"). "
		        		+ "Change threshold: " + changeThresholdPercent;
	}

    protected double getChangePercent(double currentValue, double baseLine) {
		return((Math.abs(baseLine - currentValue)*100)/baseLine);
	}

    protected boolean isCBCheckFailed(double currentValue, double changeThresholdPercent, double baseLine) {
		return (double)Math.abs(baseLine - currentValue) > (double)(baseLine * changeThresholdPercent);
	}

    protected boolean metricExists(String metricName) {
        try {
            getCassandraHelper().getVipKeyValuePair(vip, metricName);
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    public void saveSuccessSizesForCycle(long cycleVersion) {
        for(String key: successCountsForCycle.keySet()){
            try {
                getCassandraHelper().addVipKeyValuePair(vip, key, String.valueOf(successCountsForCycle.get(key)));
            } catch (ConnectionException e) {
                e.printStackTrace();
                ctx.getLogger().warn(CircuitBreaker, "Hollow validation infrastructure error:  Could not write data to C*: vip={} key={}", vip, key);
            }
        }
    }

    protected double getBaseLine(String objectName) throws NumberFormatException, ConnectionException {
        return Double.parseDouble(getCassandraHelper().getVipKeyValuePair(vip, objectName));
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

    private TransformerCassandraColumnFamilyHelper getCassandraHelper() {
        return ctx.getCassandraHelper().getColumnFamilyHelper(CIRCUITBREAKER_STATS);
    }

}
