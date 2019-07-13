package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutPhaseElements, K> uki = UniqueKeyIndex.from(consumer, RolloutPhaseElements.class)
 *         .usingBean(k);
 *     RolloutPhaseElements m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutPhaseElements} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutPhaseElementsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<RolloutAPI, RolloutPhaseElements> implements HollowUniqueKeyIndex<RolloutPhaseElements> {

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutPhaseElements")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseElementsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseElements", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutPhaseElements findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseElements(ordinal);
    }

}