package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseArtwork, K> uki = UniqueKeyIndex.from(consumer, PhaseArtwork.class)
 *         .usingBean(k);
 *     PhaseArtwork m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseArtwork} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseArtworkPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseArtwork> implements HollowUniqueKeyIndex<PhaseArtwork> {

    public PhaseArtworkPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseArtworkPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseArtwork")).getPrimaryKey().getFieldPaths());
    }

    public PhaseArtworkPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseArtworkPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseArtwork", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseArtwork findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseArtwork(ordinal);
    }

}