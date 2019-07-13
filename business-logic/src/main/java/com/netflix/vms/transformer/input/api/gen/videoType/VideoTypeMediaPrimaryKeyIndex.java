package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoTypeMedia, K> uki = UniqueKeyIndex.from(consumer, VideoTypeMedia.class)
 *         .usingBean(k);
 *     VideoTypeMedia m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoTypeMedia} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoTypeMediaPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoTypeAPI, VideoTypeMedia> implements HollowUniqueKeyIndex<VideoTypeMedia> {

    public VideoTypeMediaPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoTypeMediaPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoTypeMedia")).getPrimaryKey().getFieldPaths());
    }

    public VideoTypeMediaPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoTypeMediaPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoTypeMedia", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoTypeMedia findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoTypeMedia(ordinal);
    }

}