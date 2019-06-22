package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoNodeType, K> uki = UniqueKeyIndex.from(consumer, VideoNodeType.class)
 *         .usingBean(k);
 *     VideoNodeType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoNodeType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoNodeTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, VideoNodeType> implements HollowUniqueKeyIndex<VideoNodeType> {

    public VideoNodeTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoNodeTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoNodeType")).getPrimaryKey().getFieldPaths());
    }

    public VideoNodeTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoNodeTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoNodeType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoNodeType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoNodeType(ordinal);
    }

}