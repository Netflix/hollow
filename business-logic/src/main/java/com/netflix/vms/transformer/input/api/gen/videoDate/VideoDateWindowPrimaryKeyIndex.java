package com.netflix.vms.transformer.input.api.gen.videoDate;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoDateWindow, K> uki = UniqueKeyIndex.from(consumer, VideoDateWindow.class)
 *         .usingBean(k);
 *     VideoDateWindow m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoDateWindow} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoDateWindowPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoDateAPI, VideoDateWindow> implements HollowUniqueKeyIndex<VideoDateWindow> {

    public VideoDateWindowPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoDateWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoDateWindow")).getPrimaryKey().getFieldPaths());
    }

    public VideoDateWindowPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoDateWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoDateWindow", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoDateWindow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoDateWindow(ordinal);
    }

}