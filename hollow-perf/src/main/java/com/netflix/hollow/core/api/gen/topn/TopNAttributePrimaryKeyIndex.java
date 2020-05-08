package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<TopNAttribute, K> uki = UniqueKeyIndex.from(consumer, TopNAttribute.class)
 *         .usingBean(k);
 *     TopNAttribute m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code TopNAttribute} object.
 */
@Deprecated
@SuppressWarnings("all")
public class TopNAttributePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<TopNAPI, TopNAttribute> implements HollowUniqueKeyIndex<TopNAttribute> {

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("TopNAttribute")).getPrimaryKey().getFieldPaths());
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TopNAttributePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TopNAttribute", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public TopNAttribute findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTopNAttribute(ordinal);
    }

}