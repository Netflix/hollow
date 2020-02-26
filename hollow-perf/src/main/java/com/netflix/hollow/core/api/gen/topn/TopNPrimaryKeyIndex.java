package com.netflix.hollow.core.api.gen.topn;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<TopN, K> uki = UniqueKeyIndex.from(consumer, TopN.class)
 *         .usingBean(k);
 *     TopN m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code TopN} object.
 */
@Deprecated
@SuppressWarnings("all")
public class TopNPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<TopNAPI, TopN> implements HollowUniqueKeyIndex<TopN> {

    public TopNPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public TopNPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("TopN")).getPrimaryKey().getFieldPaths());
    }

    public TopNPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TopNPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TopN", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public TopN findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTopN(ordinal);
    }

}