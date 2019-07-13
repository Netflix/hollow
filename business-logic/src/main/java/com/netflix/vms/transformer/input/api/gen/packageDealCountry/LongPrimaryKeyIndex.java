package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<HLong, K> uki = UniqueKeyIndex.from(consumer, HLong.class)
 *         .usingBean(k);
 *     HLong m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code HLong} object.
 */
@Deprecated
@SuppressWarnings("all")
public class LongPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<PackageDealCountryAPI, HLong> implements HollowUniqueKeyIndex<HLong> {

    public LongPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public LongPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Long")).getPrimaryKey().getFieldPaths());
    }

    public LongPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public LongPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Long", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public HLong findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getHLong(ordinal);
    }

}