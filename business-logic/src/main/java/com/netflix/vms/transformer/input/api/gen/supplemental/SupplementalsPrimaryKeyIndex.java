package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Supplementals, K> uki = UniqueKeyIndex.from(consumer, Supplementals.class)
 *         .usingBean(k);
 *     Supplementals m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Supplementals} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SupplementalsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<SupplementalAPI, Supplementals> implements HollowUniqueKeyIndex<Supplementals> {

    public SupplementalsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SupplementalsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Supplementals")).getPrimaryKey().getFieldPaths());
    }

    public SupplementalsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SupplementalsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Supplementals", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Supplementals findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSupplementals(ordinal);
    }

}