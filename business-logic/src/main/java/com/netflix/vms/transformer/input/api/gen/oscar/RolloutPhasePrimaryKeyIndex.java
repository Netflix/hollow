package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutPhase, K> uki = UniqueKeyIndex.from(consumer, RolloutPhase.class)
 *         .usingBean(k);
 *     RolloutPhase m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutPhase} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutPhasePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, RolloutPhase> implements HollowUniqueKeyIndex<RolloutPhase> {

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutPhase")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhasePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhase", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutPhase findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhase(ordinal);
    }

}