package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RatingsRequirements, K> uki = UniqueKeyIndex.from(consumer, RatingsRequirements.class)
 *         .usingBean(k);
 *     RatingsRequirements m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RatingsRequirements} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RatingsRequirementsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, RatingsRequirements> implements HollowUniqueKeyIndex<RatingsRequirements> {

    public RatingsRequirementsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RatingsRequirementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RatingsRequirements")).getPrimaryKey().getFieldPaths());
    }

    public RatingsRequirementsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RatingsRequirementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RatingsRequirements", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RatingsRequirements findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRatingsRequirements(ordinal);
    }

}