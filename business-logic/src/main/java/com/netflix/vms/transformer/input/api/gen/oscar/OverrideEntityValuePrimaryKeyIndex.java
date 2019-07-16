package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<OverrideEntityValue, K> uki = UniqueKeyIndex.from(consumer, OverrideEntityValue.class)
 *         .usingBean(k);
 *     OverrideEntityValue m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code OverrideEntityValue} object.
 */
@Deprecated
@SuppressWarnings("all")
public class OverrideEntityValuePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, OverrideEntityValue> implements HollowUniqueKeyIndex<OverrideEntityValue> {

    public OverrideEntityValuePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public OverrideEntityValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("OverrideEntityValue")).getPrimaryKey().getFieldPaths());
    }

    public OverrideEntityValuePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public OverrideEntityValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "OverrideEntityValue", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public OverrideEntityValue findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getOverrideEntityValue(ordinal);
    }

}