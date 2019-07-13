package com.netflix.vms.transformer.input.api.gen.videoType;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoTypeDescriptor, K> uki = UniqueKeyIndex.from(consumer, VideoTypeDescriptor.class)
 *         .usingBean(k);
 *     VideoTypeDescriptor m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoTypeDescriptor} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoTypeDescriptorPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VideoTypeAPI, VideoTypeDescriptor> implements HollowUniqueKeyIndex<VideoTypeDescriptor> {

    public VideoTypeDescriptorPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoTypeDescriptorPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoTypeDescriptor")).getPrimaryKey().getFieldPaths());
    }

    public VideoTypeDescriptorPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoTypeDescriptorPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoTypeDescriptor", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoTypeDescriptor findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoTypeDescriptor(ordinal);
    }

}