package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoGeneralTitleTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoGeneralTitleTypeHollow> {

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoGeneralTitleType")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "VideoGeneralTitleType", isListenToDataRefreah, fieldPaths);
    }

    public VideoGeneralTitleTypeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralTitleTypeHollow(ordinal);
    }

}