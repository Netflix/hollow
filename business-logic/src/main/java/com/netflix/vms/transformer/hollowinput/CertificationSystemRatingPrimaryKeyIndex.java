package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CertificationSystemRatingPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CertificationSystemRatingHollow> {

    public CertificationSystemRatingPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public CertificationSystemRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CertificationSystemRating")).getPrimaryKey().getFieldPaths());
    }

    public CertificationSystemRatingPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CertificationSystemRatingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "CertificationSystemRating", isListenToDataRefreah, fieldPaths);
    }

    public CertificationSystemRatingHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCertificationSystemRatingHollow(ordinal);
    }

}