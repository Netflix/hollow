package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<VideoHierarchyInfo, K> uki = UniqueKeyIndex.from(consumer, VideoHierarchyInfo.class)
 *         .usingBean(k);
 *     VideoHierarchyInfo m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code VideoHierarchyInfo} object.
 */
@Deprecated
@SuppressWarnings("all")
public class VideoHierarchyInfoPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, VideoHierarchyInfo> implements HollowUniqueKeyIndex<VideoHierarchyInfo> {

    public VideoHierarchyInfoPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public VideoHierarchyInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("VideoHierarchyInfo")).getPrimaryKey().getFieldPaths());
    }

    public VideoHierarchyInfoPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public VideoHierarchyInfoPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "VideoHierarchyInfo", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public VideoHierarchyInfo findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getVideoHierarchyInfo(ordinal);
    }

}