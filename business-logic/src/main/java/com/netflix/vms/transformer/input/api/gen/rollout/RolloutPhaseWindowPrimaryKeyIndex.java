package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutPhaseWindow, K> uki = UniqueKeyIndex.from(consumer, RolloutPhaseWindow.class)
 *         .usingBean(k);
 *     RolloutPhaseWindow m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutPhaseWindow} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutPhaseWindowPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<RolloutAPI, RolloutPhaseWindow> implements HollowUniqueKeyIndex<RolloutPhaseWindow> {

    public RolloutPhaseWindowPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutPhaseWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutPhaseWindow")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseWindowPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseWindowPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseWindow", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutPhaseWindow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseWindow(ordinal);
    }

}