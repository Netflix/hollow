package com.netflix.vms.transformer.input.api.gen.videoAward;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoAwardMapping, K> uki = UniqueKeyIndex.from(consumer, VideoAwardMapping.class)
 *         .usingBean(k);
 *     VideoAwardMapping m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoAwardMapping} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoAwardMappingPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoAwardAPI, VideoAwardMapping> implements HollowUniqueKeyIndex<VideoAwardMapping> {

    public VideoAwardMappingPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoAwardMappingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoAwardMapping")).getPrimaryKey().getFieldPaths());
    }

    public VideoAwardMappingPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoAwardMappingPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoAwardMapping", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoAwardMapping findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoAwardMapping(ordinal);
    }

}