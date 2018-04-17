package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VideoStreamCropParamsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, VideoStreamCropParamsHollow> {

    public VideoStreamCropParamsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public VideoStreamCropParamsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("VideoStreamCropParams")).getPrimaryKey().getFieldPaths());
    }

    public VideoStreamCropParamsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoStreamCropParamsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoStreamCropParams", isListenToDataRefresh, fieldPaths);
    }

    public VideoStreamCropParamsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoStreamCropParamsHollow(ordinal);
    }

}