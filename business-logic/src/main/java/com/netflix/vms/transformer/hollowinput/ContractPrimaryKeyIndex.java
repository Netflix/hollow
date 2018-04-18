package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ContractPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ContractHollow> {

    public ContractPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ContractPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Contract")).getPrimaryKey().getFieldPaths());
    }

    public ContractPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ContractPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Contract", isListenToDataRefresh, fieldPaths);
    }

    public ContractHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getContractHollow(ordinal);
    }

}