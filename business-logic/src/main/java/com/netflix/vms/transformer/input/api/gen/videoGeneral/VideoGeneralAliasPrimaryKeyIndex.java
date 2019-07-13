package com.netflix.vms.transformer.input.api.gen.videoGeneral;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoGeneralAlias, K> uki = UniqueKeyIndex.from(consumer, VideoGeneralAlias.class)
 *         .usingBean(k);
 *     VideoGeneralAlias m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoGeneralAlias} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoGeneralAliasPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoGeneralAPI, VideoGeneralAlias> implements HollowUniqueKeyIndex<VideoGeneralAlias> {

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoGeneralAlias")).getPrimaryKey().getFieldPaths());
    }

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoGeneralAliasPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoGeneralAlias", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoGeneralAlias findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoGeneralAlias(ordinal);
    }

}