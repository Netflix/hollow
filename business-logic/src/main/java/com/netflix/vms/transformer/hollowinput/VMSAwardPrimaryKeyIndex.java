package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VMSAwardPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VMSAwardHollow> {

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VMSAward")).getPrimaryKey().getFieldPaths());
    }

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VMSAwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VMSAward", isListenToDataRefresh, fieldPaths);
    }

    public VMSAwardHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVMSAwardHollow(ordinal);
    }

}