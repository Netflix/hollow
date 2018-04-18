package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DeployablePackagesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DeployablePackagesHollow> {

    public DeployablePackagesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public DeployablePackagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DeployablePackages")).getPrimaryKey().getFieldPaths());
    }

    public DeployablePackagesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DeployablePackagesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DeployablePackages", isListenToDataRefresh, fieldPaths);
    }

    public DeployablePackagesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDeployablePackagesHollow(ordinal);
    }

}