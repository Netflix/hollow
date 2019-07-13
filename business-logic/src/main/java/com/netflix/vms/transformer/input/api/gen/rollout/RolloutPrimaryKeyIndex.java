package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Rollout, K> uki = UniqueKeyIndex.from(consumer, Rollout.class)
 *         .usingBean(k);
 *     Rollout m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Rollout} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<RolloutAPI, Rollout> implements HollowUniqueKeyIndex<Rollout> {

    public RolloutPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Rollout")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Rollout", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Rollout findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRollout(ordinal);
    }

}