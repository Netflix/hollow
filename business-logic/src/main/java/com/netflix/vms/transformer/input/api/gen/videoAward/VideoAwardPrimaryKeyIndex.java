package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoAward, K> uki = UniqueKeyIndex.from(consumer, VideoAward.class)
 *         .usingBean(k);
 *     VideoAward m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoAward} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoAwardPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoAwardAPI, VideoAward> implements HollowUniqueKeyIndex<VideoAward> {

    public VideoAwardPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoAwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoAward")).getPrimaryKey().getFieldPaths());
    }

    public VideoAwardPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoAwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoAward", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoAward findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoAward(ordinal);
    }

}