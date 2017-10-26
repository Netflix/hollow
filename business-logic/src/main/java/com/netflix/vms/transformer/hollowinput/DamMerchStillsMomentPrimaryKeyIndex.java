package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DamMerchStillsMomentPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DamMerchStillsMomentHollow> {

    public DamMerchStillsMomentPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DamMerchStillsMoment")).getPrimaryKey().getFieldPaths());
    }

    public DamMerchStillsMomentPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DamMerchStillsMomentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "DamMerchStillsMoment", isListenToDataRefreah, fieldPaths);
    }

    public DamMerchStillsMomentHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDamMerchStillsMomentHollow(ordinal);
    }

}