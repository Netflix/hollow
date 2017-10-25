package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, IPLArtworkDerivativeHollow> {

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("IPLArtworkDerivative")).getPrimaryKey().getFieldPaths());
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "IPLArtworkDerivative", isListenToDataRefreah, fieldPaths);
    }

    public IPLArtworkDerivativeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLArtworkDerivativeHollow(ordinal);
    }

}