package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Award, K> uki = UniqueKeyIndex.from(consumer, Award.class)
 *         .usingBean(k);
 *     Award m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Award} object.
 */
@Deprecated
@SuppressWarnings("all")
public class AwardPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<AwardsAPI, Award> implements HollowUniqueKeyIndex<Award> {

    public AwardPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public AwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Award")).getPrimaryKey().getFieldPaths());
    }

    public AwardPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AwardPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Award", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Award findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAward(ordinal);
    }

}