package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RightsContractHollow> {

    public RightsContractPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RightsContract")).getPrimaryKey().getFieldPaths());
    }

    public RightsContractPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsContractPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "RightsContract", isListenToDataRefreah, fieldPaths);
    }

    public RightsContractHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsContractHollow(ordinal);
    }

}