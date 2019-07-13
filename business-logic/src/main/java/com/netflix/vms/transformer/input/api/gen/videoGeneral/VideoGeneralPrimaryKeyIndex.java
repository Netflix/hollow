package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoGeneral, K> uki = UniqueKeyIndex.from(consumer, VideoGeneral.class)
 *         .usingBean(k);
 *     VideoGeneral m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoGeneral} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoGeneralPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoGeneralAPI, VideoGeneral> implements HollowUniqueKeyIndex<VideoGeneral> {

    public VideoGeneralPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoGeneralPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoGeneral")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneral", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoGeneral findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneral(ordinal);
    }

}