package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.vms.generated.notemplate.CompleteVideoHollow;
import com.netflix.vms.generated.notemplate.ISOCountryHollow;
import com.netflix.vms.generated.notemplate.VMSRawHollowAPI;
import com.netflix.vms.transformer.common.TransformerContext;
import java.util.BitSet;

public abstract class HollowPerCountryCompleteVideoScoringCircuitBreaker extends HollowCountrySpecificCircuitBreaker {

    public HollowPerCountryCompleteVideoScoringCircuitBreaker(TransformerContext ctx, String vip, long versionId) {
        super(ctx, vip, versionId);
    }

    @Override
    protected final CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        VMSRawHollowAPI hollowApi = new VMSRawHollowAPI(stateEngine);

        int perCountryCertificationCounts[] = new int[stateEngine.getTypeState("ISOCountry").maxOrdinal() + 1];

        HollowTypeReadState typeState = stateEngine.getTypeState("CompleteVideo");

        PopulatedOrdinalListener listener = typeState.getListener(PopulatedOrdinalListener.class);
        BitSet populatedOrdinals = listener.getPopulatedOrdinals();

        int ordinal = populatedOrdinals.nextSetBit(0);
        while(ordinal != -1) {
            CompleteVideoHollow cv = hollowApi.getCompleteVideoHollow(ordinal);

            int countryOrdinal = cv.typeApi().getCountryOrdinal(ordinal);
            perCountryCertificationCounts[countryOrdinal] += getVideoScore(cv);

            ordinal = populatedOrdinals.nextSetBit(ordinal + 1);
        }

        CircuitBreakerResults results = new CircuitBreakerResults();

        for(int i=0;i<perCountryCertificationCounts.length;i++) {
            if(stateEngine.getTypeState("ISOCountry").getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals().get(i)) {
                ISOCountryHollow country = hollowApi.getISOCountryHollow(i);
                if(isEnabled(country._getId()))
                    results.addResult(compareMetric(country._getId(), perCountryCertificationCounts[i]));
            }
        }

        return results;
    }

    protected abstract int getVideoScore(CompleteVideoHollow cv);

}
