package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<OverrideEntityType, K> uki = UniqueKeyIndex.from(consumer, OverrideEntityType.class)
 *         .usingBean(k);
 *     OverrideEntityType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code OverrideEntityType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class OverrideEntityTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, OverrideEntityType> implements HollowUniqueKeyIndex<OverrideEntityType> {

    public OverrideEntityTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public OverrideEntityTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("OverrideEntityType")).getPrimaryKey().getFieldPaths());
    }

    public OverrideEntityTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public OverrideEntityTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "OverrideEntityType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public OverrideEntityType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getOverrideEntityType(ordinal);
    }

}