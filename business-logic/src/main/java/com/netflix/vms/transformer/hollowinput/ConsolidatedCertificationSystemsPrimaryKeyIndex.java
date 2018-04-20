package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ConsolidatedCertificationSystemsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ConsolidatedCertificationSystemsHollow> {

    public ConsolidatedCertificationSystemsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ConsolidatedCertificationSystemsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("ConsolidatedCertificationSystems")).getPrimaryKey().getFieldPaths());
    }

    public ConsolidatedCertificationSystemsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ConsolidatedCertificationSystemsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ConsolidatedCertificationSystems", isListenToDataRefresh, fieldPaths);
    }

    public ConsolidatedCertificationSystemsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getConsolidatedCertificationSystemsHollow(ordinal);
    }

}