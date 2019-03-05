package com.netflix.vms.transformer.publish.workflow.circuitbreaker;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.hollow.core.read.engine.HollowTypeReadState;
import com.netflix.hollow.core.read.engine.PopulatedOrdinalListener;
import com.netflix.vms.transformer.common.TransformerContext;
import java.util.BitSet;

public class TypeCardinalityCircuitBreaker extends HollowCircuitBreaker {

    private final String typeName;

    public TypeCardinalityCircuitBreaker(TransformerContext ctx, String vip, long versionId, String typeName) {
        super(ctx, vip, versionId);
        this.typeName = typeName;
    }

    @Override
    public String getRuleName() {
        return typeName + "Cardinality";
    }

    @Override
    protected CircuitBreakerResults runCircuitBreaker(HollowReadStateEngine stateEngine) {
        HollowTypeReadState typeState = stateEngine.getTypeState(typeName);
        BitSet populatedOrdinals = typeState.getListener(PopulatedOrdinalListener.class).getPopulatedOrdinals();
        int currentCardinality = populatedOrdinals.cardinality();

        return compareMetric(currentCardinality);
    }

}
