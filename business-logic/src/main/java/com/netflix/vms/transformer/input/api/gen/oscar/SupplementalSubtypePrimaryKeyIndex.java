package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<SupplementalSubtype, K> uki = UniqueKeyIndex.from(consumer, SupplementalSubtype.class)
 *         .usingBean(k);
 *     SupplementalSubtype m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code SupplementalSubtype} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SupplementalSubtypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, SupplementalSubtype> implements HollowUniqueKeyIndex<SupplementalSubtype> {

    public SupplementalSubtypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SupplementalSubtypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("SupplementalSubtype")).getPrimaryKey().getFieldPaths());
    }

    public SupplementalSubtypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SupplementalSubtypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "SupplementalSubtype", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public SupplementalSubtype findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSupplementalSubtype(ordinal);
    }

}