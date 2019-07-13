package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<HBoolean, K> uki = UniqueKeyIndex.from(consumer, HBoolean.class)
 *         .usingBean(k);
 *     HBoolean m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code HBoolean} object.
 */
@Deprecated
@SuppressWarnings("all")
public class BooleanPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<PackageDealCountryAPI, HBoolean> implements HollowUniqueKeyIndex<HBoolean> {

    public BooleanPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public BooleanPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Boolean")).getPrimaryKey().getFieldPaths());
    }

    public BooleanPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public BooleanPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Boolean", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public HBoolean findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getHBoolean(ordinal);
    }

}