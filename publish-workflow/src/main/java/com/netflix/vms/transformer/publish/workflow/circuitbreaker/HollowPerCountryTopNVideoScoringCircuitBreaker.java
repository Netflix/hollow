package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import static com.netflix.vms.transformer.common.io.TransformerLogTag.CircuitBreaker;

import com.netflix.hollow.core.index.HollowPrimaryKeyIndex;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.FloatHollow;
import com.netflix.vms.generated.notemplate.IntegerHollow;
import com.netflix.vms.generated.notemplate.MapOfIntegerToFloatHollow;
import com.netflix.vms.generated.notemplate.TopNVideoDataHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.common.TransformerMetricRecorder.Metric;
import com.netflix.vms.transformer.publish.workflow.logmessage.ViewShareMessage;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

public abstract class HollowPerCountryTopNVideoScoringCircuitBreaker extends HollowFixedBaseLineCountrySpecificCircuitBreaker {

	public HollowPerCountryTopNVideoScoringCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
		super(ctx, vip, versionId);
	}
	
    @Override
    protected final CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
    	CircuitBreakerResults results = new CircuitBreakerResults();
    	
		VMSRawHollowAPI api = new VMSRawHollowAPI(stateEngine);
		
		HollowPrimaryKeyIndex idx = new HollowPrimaryKeyIndex(stateEngine, "CompleteVideo", "id.value", "country.id");
		
		for(TopNVideoDataHollow topn: api.getAllTopNVideoDataHollow()){
			
			MapOfIntegerToFloatHollow videoViewHrsHollow = topn._getVideoViewHrs1Day();
			
			float countryViewHrs1Day = topn._getCountryViewHrs1Day();
			
			String countryId = topn._getCountryId();
			
			if(!isEnabled(countryId))
				continue;
			
			if(videoViewHrsHollow != null && Float.compare(0, countryViewHrs1Day)!=0){
			
				float missingViewShare = 0f;
				Set<Integer> missingVideoIDs = new HashSet<>();
				for(Entry<IntegerHollow, FloatHollow> entryH: videoViewHrsHollow.entrySet()){
					
					int videoId = entryH.getKey()._getVal();
					int compVideoOrdinal = idx.getMatchingOrdinal(videoId, countryId);

					int videoScore = compVideoOrdinal == -1 ? 0 : getVideoScore(api.getCompleteVideoHollow(compVideoOrdinal));
					if(videoScore < 1){
						float videoViewShare = (entryH.getValue()._getVal()/countryViewHrs1Day)*100;
						missingViewShare += videoViewShare;
						missingVideoIDs.add(videoId);
					}
				}
				results.addResult(compareMetric(countryId, missingViewShare));
				if(missingVideoIDs.size() > 0)
                    ctx.getLogger().warn(CircuitBreaker, new ViewShareMessage("TopNViewShare", countryId, missingVideoIDs, missingViewShare, null));
				ctx.getMetricRecorder().recordMetric(Metric.TopNMissingViewShare, missingViewShare, "country", countryId);
			}
		}
        return results;
    }

	protected abstract int getVideoScore(CompleteVideoHollow cv);
}
