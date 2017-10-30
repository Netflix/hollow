package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLDerivativeGroupPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, IPLDerivativeGroupHollow> {

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("IPLDerivativeGroup")).getPrimaryKey().getFieldPaths());
    }

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLDerivativeGroupPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "IPLDerivativeGroup", isListenToDataRefreah, fieldPaths);
    }

    public IPLDerivativeGroupHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLDerivativeGroupHollow(ordinal);
    }

}