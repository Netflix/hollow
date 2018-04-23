package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSetPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, IPLArtworkDerivativeSetHollow> {

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("IPLArtworkDerivativeSet")).getPrimaryKey().getFieldPaths());
    }

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IPLArtworkDerivativeSetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "IPLArtworkDerivativeSet", isListenToDataRefresh, fieldPaths);
    }

    public IPLArtworkDerivativeSetHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIPLArtworkDerivativeSetHollow(ordinal);
    }

}