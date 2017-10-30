package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class RightsContractPackagePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, RightsContractPackageHollow> {

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("RightsContractPackage")).getPrimaryKey().getFieldPaths());
    }

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RightsContractPackagePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "RightsContractPackage", isListenToDataRefreah, fieldPaths);
    }

    public RightsContractPackageHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRightsContractPackageHollow(ordinal);
    }

}