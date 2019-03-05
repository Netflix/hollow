package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.vms.transformer.common.TransformerContext;

public abstract class HollowFixedBaseLineCountrySpecificCircuitBreaker extends HollowCountrySpecificCircuitBreaker {

	public HollowFixedBaseLineCountrySpecificCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
		super(ctx, vip, versionId);
	}

	@Override
    protected boolean isCBCheckFailed(double currentValue, double changeThresholdPercent, double baseLine) {
    	return (currentValue > changeThresholdPercent);
    }
    
	@Override
    protected double getBaseLine(String objectName) throws NumberFormatException, ConnectionException {
    	return 0;
    }
		
    @Override
    protected String getFailedCBMessage(String metricName, double currentValue, double changeThresholdPercent, double baseLine) {
		return "Hollow validation failure for " + metricName + ": "
		        + "This will result failure of announcement of data to clients."
		        + "Observed a value of  " + currentValue + " percent which is more than threshold: " + changeThresholdPercent+" percentage.";
	}
    
    @Override
    public void saveSuccessSizesForCycle(long cycleVersion) {
    	// Do nothing. Since baseline is not used, the success counts per cycle need not be saved.  
    }
    
    @Override
	protected String getSuccessMessage(String metricName, double currentValue, double changeThresholdPercent, double baseLine) {
		return "Metric \"" + metricName + "\" current value: " + currentValue + " threshold: " + changeThresholdPercent;
	}
}
