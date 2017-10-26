package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DerivativeTagPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DerivativeTagHollow> {

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DerivativeTag")).getPrimaryKey().getFieldPaths());
    }

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DerivativeTagPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "DerivativeTag", isListenToDataRefreah, fieldPaths);
    }

    public DerivativeTagHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDerivativeTagHollow(ordinal);
    }

}