package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<ForceReason, K> uki = UniqueKeyIndex.from(consumer, ForceReason.class)
 *         .usingBean(k);
 *     ForceReason m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code ForceReason} object.
 */
@Deprecated
@SuppressWarnings("all")
public class ForceReasonPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, ForceReason> implements HollowUniqueKeyIndex<ForceReason> {

    public ForceReasonPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public ForceReasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("ForceReason")).getPrimaryKey().getFieldPaths());
    }

    public ForceReasonPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public ForceReasonPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "ForceReason", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public ForceReason findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getForceReason(ordinal);
    }

}