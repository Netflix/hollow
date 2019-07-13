package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoType, K> uki = UniqueKeyIndex.from(consumer, VideoType.class)
 *         .usingBean(k);
 *     VideoType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoTypeAPI, VideoType> implements HollowUniqueKeyIndex<VideoType> {

    public VideoTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoType")).getPrimaryKey().getFieldPaths());
    }

    public VideoTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoType(ordinal);
    }

}