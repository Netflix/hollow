package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoDate, K> uki = UniqueKeyIndex.from(consumer, VideoDate.class)
 *         .usingBean(k);
 *     VideoDate m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoDate} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoDatePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoDateAPI, VideoDate> implements HollowUniqueKeyIndex<VideoDate> {

    public VideoDatePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoDatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoDate")).getPrimaryKey().getFieldPaths());
    }

    public VideoDatePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoDatePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoDate", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoDate findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoDate(ordinal);
    }

}