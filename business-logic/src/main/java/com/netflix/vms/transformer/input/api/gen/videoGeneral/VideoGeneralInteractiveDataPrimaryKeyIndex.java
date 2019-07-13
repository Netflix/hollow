package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoGeneralInteractiveData, K> uki = UniqueKeyIndex.from(consumer, VideoGeneralInteractiveData.class)
 *         .usingBean(k);
 *     VideoGeneralInteractiveData m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoGeneralInteractiveData} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoGeneralInteractiveDataPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoGeneralAPI, VideoGeneralInteractiveData> implements HollowUniqueKeyIndex<VideoGeneralInteractiveData> {

    public VideoGeneralInteractiveDataPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoGeneralInteractiveDataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoGeneralInteractiveData")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralInteractiveDataPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralInteractiveDataPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneralInteractiveData", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoGeneralInteractiveData findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralInteractiveData(ordinal);
    }

}