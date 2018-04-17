package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralTitleTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoGeneralTitleTypeHollow> {

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoGeneralTitleType")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneralTitleType", isListenToDataRefresh, fieldPaths);
    }

    public VideoGeneralTitleTypeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralTitleTypeHollow(ordinal);
    }

}