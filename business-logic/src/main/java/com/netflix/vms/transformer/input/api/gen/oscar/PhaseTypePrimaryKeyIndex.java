package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseType, K> uki = UniqueKeyIndex.from(consumer, PhaseType.class)
 *         .usingBean(k);
 *     PhaseType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseType> implements HollowUniqueKeyIndex<PhaseType> {

    public PhaseTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseType")).getPrimaryKey().getFieldPaths());
    }

    public PhaseTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseType(ordinal);
    }

}