package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutPhaseArtwork, K> uki = UniqueKeyIndex.from(consumer, RolloutPhaseArtwork.class)
 *         .usingBean(k);
 *     RolloutPhaseArtwork m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutPhaseArtwork} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutPhaseArtworkPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<RolloutAPI, RolloutPhaseArtwork> implements HollowUniqueKeyIndex<RolloutPhaseArtwork> {

    public RolloutPhaseArtworkPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutPhaseArtworkPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutPhaseArtwork")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseArtworkPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseArtworkPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseArtwork", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutPhaseArtwork findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseArtwork(ordinal);
    }

}