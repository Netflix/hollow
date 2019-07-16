package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutType, K> uki = UniqueKeyIndex.from(consumer, RolloutType.class)
 *         .usingBean(k);
 *     RolloutType m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutType} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutTypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, RolloutType> implements HollowUniqueKeyIndex<RolloutType> {

    public RolloutTypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutType")).getPrimaryKey().getFieldPaths());
    }

    public RolloutTypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutTypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutType", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutType findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutType(ordinal);
    }

}