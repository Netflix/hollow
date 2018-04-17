package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class StreamDeploymentLabelPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, StreamDeploymentLabelHollow> {

    public StreamDeploymentLabelPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public StreamDeploymentLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("StreamDeploymentLabel")).getPrimaryKey().getFieldPaths());
    }

    public StreamDeploymentLabelPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StreamDeploymentLabelPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "StreamDeploymentLabel", isListenToDataRefresh, fieldPaths);
    }

    public StreamDeploymentLabelHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStreamDeploymentLabelHollow(ordinal);
    }

}