package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralAliasPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoGeneralAliasHollow> {

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoGeneralAlias")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneralAlias", isListenToDataRefresh, fieldPaths);
    }

    public VideoGeneralAliasHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralAliasHollow(ordinal);
    }

}