package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, IPLArtworkDerivativeHollow> {

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("IPLArtworkDerivative")).getPrimaryKey().getFieldPaths());
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLArtworkDerivativePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "IPLArtworkDerivative", isListenToDataRefresh, fieldPaths);
    }

    public IPLArtworkDerivativeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLArtworkDerivativeHollow(ordinal);
    }

}