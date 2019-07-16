package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<PhaseRequiredImageType, K> uki = UniqueKeyIndex.from(consumer, PhaseRequiredImageType.class)
 *         .usingBean(k);
 *     PhaseRequiredImageType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code PhaseRequiredImageType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class PhaseRequiredImageTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, PhaseRequiredImageType> implements HollowUniqueKeyIndex<PhaseRequiredImageType> {

    public PhaseRequiredImageTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public PhaseRequiredImageTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("PhaseRequiredImageType")).getPrimaryKey().getFieldPaths());
    }

    public PhaseRequiredImageTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public PhaseRequiredImageTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "PhaseRequiredImageType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public PhaseRequiredImageType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getPhaseRequiredImageType(ordinal);
    }

}