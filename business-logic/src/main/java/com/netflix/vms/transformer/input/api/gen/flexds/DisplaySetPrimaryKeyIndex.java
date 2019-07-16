package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<DisplaySet, K> uki = UniqueKeyIndex.from(consumer, DisplaySet.class)
 *         .usingBean(k);
 *     DisplaySet m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code DisplaySet} object.
 */
@Deprecated
@SuppressWarnings("all")
public class DisplaySetPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<FlexDSAPI, DisplaySet> implements HollowUniqueKeyIndex<DisplaySet> {

    public DisplaySetPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public DisplaySetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("DisplaySet")).getPrimaryKey().getFieldPaths());
    }

    public DisplaySetPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DisplaySetPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DisplaySet", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public DisplaySet findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDisplaySet(ordinal);
    }

}