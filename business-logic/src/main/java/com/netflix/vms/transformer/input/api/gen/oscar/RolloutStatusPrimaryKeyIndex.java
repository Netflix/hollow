package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<RolloutStatus, K> uki = UniqueKeyIndex.from(consumer, RolloutStatus.class)
 *         .usingBean(k);
 *     RolloutStatus m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code RolloutStatus} object.
 */
@Deprecated
@SuppressWarnings("all")
public class RolloutStatusPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, RolloutStatus> implements HollowUniqueKeyIndex<RolloutStatus> {

    public RolloutStatusPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public RolloutStatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("RolloutStatus")).getPrimaryKey().getFieldPaths());
    }

    public RolloutStatusPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public RolloutStatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "RolloutStatus", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public RolloutStatus findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getRolloutStatus(ordinal);
    }

}