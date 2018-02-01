package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ContractsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ContractsHollow> {

    public ContractsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public ContractsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Contracts")).getPrimaryKey().getFieldPaths());
    }

    public ContractsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ContractsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Contracts", isListenToDataRefresh, fieldPaths);
    }

    public ContractsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getContractsHollow(ordinal);
    }

}