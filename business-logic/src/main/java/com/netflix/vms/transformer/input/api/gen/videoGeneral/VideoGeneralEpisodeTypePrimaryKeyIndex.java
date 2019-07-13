package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoGeneralEpisodeType, K> uki = UniqueKeyIndex.from(consumer, VideoGeneralEpisodeType.class)
 *         .usingBean(k);
 *     VideoGeneralEpisodeType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoGeneralEpisodeType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoGeneralEpisodeTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoGeneralAPI, VideoGeneralEpisodeType> implements HollowUniqueKeyIndex<VideoGeneralEpisodeType> {

    public VideoGeneralEpisodeTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoGeneralEpisodeTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoGeneralEpisodeType")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralEpisodeTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralEpisodeTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneralEpisodeType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoGeneralEpisodeType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralEpisodeType(ordinal);
    }

}