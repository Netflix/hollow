package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.VMSAvailabilityWindowHollow;
import com.netflix.vms.transformer.common.TransformerContext;
import java.util.List;

public class TopNViewShareAvailabilityCircuitBreaker extends HollowPerCountryTopNVideoScoringCircuitBreaker {

	public TopNViewShareAvailabilityCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
		super(ctx, vip, versionId);
	}

	/**
	 * isAvailableForED is implemented on client side. This is repetition of the same logic.
	 * Expectation is that this implementation is same as the client side one so the circuit breaker is most effective.
	 */
	@Override
	protected int getVideoScore(CompleteVideoHollow cv) {
		if(!cv._getData()._getFacetData()._getVideoMediaData()._getIsGoLive())
			return 0;

		List<VMSAvailabilityWindowHollow> availabilityWindowsHollow = cv._getData()._getCountrySpecificData()._getAvailabilityWindows();
		if(availabilityWindowsHollow == null || availabilityWindowsHollow.isEmpty())
			return 0;
		
		long nowMillis = ctx.getNowMillis();
		for(VMSAvailabilityWindowHollow window: availabilityWindowsHollow){
			if (window._getEndDate()._getVal() > nowMillis) {
				return 1;
			}
		}
		return 0;
	}

	@Override
	public String getRuleName() {
		return "TopNMissingViewShare";
	}

}
