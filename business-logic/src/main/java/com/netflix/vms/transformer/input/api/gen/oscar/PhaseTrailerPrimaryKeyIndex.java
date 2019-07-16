package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseTrailer, K> uki = UniqueKeyIndex.from(consumer, PhaseTrailer.class)
 *         .usingBean(k);
 *     PhaseTrailer m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseTrailer} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseTrailerPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseTrailer> implements HollowUniqueKeyIndex<PhaseTrailer> {

    public PhaseTrailerPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseTrailerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseTrailer")).getPrimaryKey().getFieldPaths());
    }

    public PhaseTrailerPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseTrailerPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseTrailer", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseTrailer findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseTrailer(ordinal);
    }

}