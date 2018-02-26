package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ContractsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, ContractsHollow> {

    public ContractsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public ContractsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Contracts")).getPrimaryKey().getFieldPaths());
    }

    public ContractsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ContractsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "Contracts", isListenToDataRefreah, fieldPaths);
    }

    public ContractsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getContractsHollow(ordinal);
    }

}