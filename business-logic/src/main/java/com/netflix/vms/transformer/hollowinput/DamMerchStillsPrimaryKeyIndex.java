package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DamMerchStillsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DamMerchStillsHollow> {

    public DamMerchStillsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DamMerchStills")).getPrimaryKey().getFieldPaths());
    }

    public DamMerchStillsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DamMerchStillsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "DamMerchStills", isListenToDataRefreah, fieldPaths);
    }

    public DamMerchStillsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDamMerchStillsHollow(ordinal);
    }

}