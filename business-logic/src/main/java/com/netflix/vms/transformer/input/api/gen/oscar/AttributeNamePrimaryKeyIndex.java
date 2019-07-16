package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<AttributeName, K> uki = UniqueKeyIndex.from(consumer, AttributeName.class)
 *         .usingBean(k);
 *     AttributeName m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code AttributeName} object.
 */
@Deprecated
@SuppressWarnings("all")
public class AttributeNamePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, AttributeName> implements HollowUniqueKeyIndex<AttributeName> {

    public AttributeNamePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public AttributeNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("AttributeName")).getPrimaryKey().getFieldPaths());
    }

    public AttributeNamePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AttributeNamePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "AttributeName", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public AttributeName findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAttributeName(ordinal);
    }

}