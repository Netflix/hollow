package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<Subtype, K> uki = UniqueKeyIndex.from(consumer, Subtype.class)
 *         .usingBean(k);
 *     Subtype m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code Subtype} object.
 */
@Deprecated
@SuppressWarnings("all")
public class SubtypePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, Subtype> implements HollowUniqueKeyIndex<Subtype> {

    public SubtypePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public SubtypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("Subtype")).getPrimaryKey().getFieldPaths());
    }

    public SubtypePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public SubtypePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Subtype", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public Subtype findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getSubtype(ordinal);
    }

}