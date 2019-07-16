package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<AttributeValue, K> uki = UniqueKeyIndex.from(consumer, AttributeValue.class)
 *         .usingBean(k);
 *     AttributeValue m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code AttributeValue} object.
 */
@Deprecated
@SuppressWarnings("all")
public class AttributeValuePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<OscarAPI, AttributeValue> implements HollowUniqueKeyIndex<AttributeValue> {

    public AttributeValuePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public AttributeValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("AttributeValue")).getPrimaryKey().getFieldPaths());
    }

    public AttributeValuePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public AttributeValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "AttributeValue", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public AttributeValue findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getAttributeValue(ordinal);
    }

}