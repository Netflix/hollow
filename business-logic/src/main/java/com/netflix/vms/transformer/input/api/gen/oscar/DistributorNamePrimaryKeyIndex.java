package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<DistributorName, K> uki = UniqueKeyIndex.from(consumer, DistributorName.class)
 *         .usingBean(k);
 *     DistributorName m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code DistributorName} object.
 */
@Deprecated
@SuppressWarnings("all")
public class DistributorNamePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, DistributorName> implements HollowUniqueKeyIndex<DistributorName> {

    public DistributorNamePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public DistributorNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("DistributorName")).getPrimaryKey().getFieldPaths());
    }

    public DistributorNamePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DistributorNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DistributorName", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public DistributorName findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDistributorName(ordinal);
    }

}