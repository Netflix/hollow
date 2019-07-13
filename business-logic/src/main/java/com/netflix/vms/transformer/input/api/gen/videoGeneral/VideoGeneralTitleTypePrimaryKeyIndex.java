package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoGeneralTitleType, K> uki = UniqueKeyIndex.from(consumer, VideoGeneralTitleType.class)
 *         .usingBean(k);
 *     VideoGeneralTitleType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoGeneralTitleType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoGeneralTitleTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoGeneralAPI, VideoGeneralTitleType> implements HollowUniqueKeyIndex<VideoGeneralTitleType> {

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoGeneralTitleType")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralTitleTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneralTitleType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoGeneralTitleType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralTitleType(ordinal);
    }

}