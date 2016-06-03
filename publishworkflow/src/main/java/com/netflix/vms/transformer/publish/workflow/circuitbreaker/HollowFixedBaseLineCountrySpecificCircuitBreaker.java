package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.vms.transformer.publish.workflow.PublishWorkflowContext;

public abstract class HollowFixedBaseLineCountrySpecificCircuitBreaker extends HollowCountrySpecificCircuitBreaker {

	public HollowFixedBaseLineCountrySpecificCircuitBreaker(PublishWorkflowContext ctx, long versionId) {
		super(ctx, versionId);
	}

    protected boolean isCBCheckFailed(double currentValue, double changeThresholdPercent, long baseLine) {
    	return (currentValue > changeThresholdPercent);
    }
    
    protected long getBaseLine(String objectName) throws NumberFormatException, ConnectionException {
    	return 0;
    }
		
    protected String getFailedCBMessage(String metricName, double currentValue, double changeThresholdPercent, long baseLine) {
		return "Hollow validation failure for " + metricName + ": "
		        + "This will result failure of announcement of data to clients."
		        + "Observed a value of  " + currentValue + " percent which is more than threshold: " + changeThresholdPercent+" percentage.";
	}
}
