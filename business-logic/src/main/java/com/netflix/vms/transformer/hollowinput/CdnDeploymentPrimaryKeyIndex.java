package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CdnDeploymentPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CdnDeploymentHollow> {

    public CdnDeploymentPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CdnDeploymentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CdnDeployment")).getPrimaryKey().getFieldPaths());
    }

    public CdnDeploymentPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CdnDeploymentPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CdnDeployment", isListenToDataRefresh, fieldPaths);
    }

    public CdnDeploymentHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCdnDeploymentHollow(ordinal);
    }

}