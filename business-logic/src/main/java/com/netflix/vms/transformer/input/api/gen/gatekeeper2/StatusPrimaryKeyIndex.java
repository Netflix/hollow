package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Status, K> uki = UniqueKeyIndex.from(consumer, Status.class)
 *         .usingBean(k);
 *     Status m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Status} object.
 */
@Deprecated
@SuppressWarnings("all")
public class StatusPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<Gk2StatusAPI, Status> implements HollowUniqueKeyIndex<Status> {

    public StatusPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Status")).getPrimaryKey().getFieldPaths());
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public StatusPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Status", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Status findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getStatus(ordinal);
    }

}