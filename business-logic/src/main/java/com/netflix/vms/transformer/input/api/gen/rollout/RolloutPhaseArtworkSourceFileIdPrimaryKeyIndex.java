package com.netflix.vms.transformer.input.api.gen.rollout;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutPhaseArtworkSourceFileId, K> uki = UniqueKeyIndex.from(consumer, RolloutPhaseArtworkSourceFileId.class)
 *         .usingBean(k);
 *     RolloutPhaseArtworkSourceFileId m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutPhaseArtworkSourceFileId} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<RolloutAPI, RolloutPhaseArtworkSourceFileId> implements HollowUniqueKeyIndex<RolloutPhaseArtworkSourceFileId> {

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutPhaseArtworkSourceFileId")).getPrimaryKey().getFieldPaths());
    }

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutPhaseArtworkSourceFileIdPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutPhaseArtworkSourceFileId", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutPhaseArtworkSourceFileId findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutPhaseArtworkSourceFileId(ordinal);
    }

}