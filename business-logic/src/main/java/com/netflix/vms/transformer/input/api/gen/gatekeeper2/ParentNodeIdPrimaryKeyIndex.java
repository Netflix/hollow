package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ParentNodeId, K> uki = UniqueKeyIndex.from(consumer, ParentNodeId.class)
 *         .usingBean(k);
 *     ParentNodeId m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ParentNodeId} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ParentNodeIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, ParentNodeId> implements HollowUniqueKeyIndex<ParentNodeId> {

    public ParentNodeIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ParentNodeIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ParentNodeId")).getPrimaryKey().getFieldPaths());
    }

    public ParentNodeIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ParentNodeIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ParentNodeId", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ParentNodeId findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getParentNodeId(ordinal);
    }

}