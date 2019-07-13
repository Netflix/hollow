package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.api.consumer.index.HollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

/**
 * @deprecated see {@link com.netflix.hollow.api.consumer.index.UniqueKeyIndex} which can be built as follows:
 * <pre>{@code
 *     UniqueKeyIndex<TranslatedTextValue, K> uki = UniqueKeyIndex.from(consumer, TranslatedTextValue.class)
 *         .usingBean(k);
 *     TranslatedTextValue m = uki.findMatch(k);
 * }</pre>
 * where {@code K} is a class declaring key field paths members, annotated with
 * {@link com.netflix.hollow.api.consumer.index.FieldPath}, and {@code k} is an instance of
 * {@code K} that is the key to find the unique {@code TranslatedTextValue} object.
 */
@Deprecated
@SuppressWarnings("all")
public class TranslatedTextValuePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<LocalizedMetaDataAPI, TranslatedTextValue> implements HollowUniqueKeyIndex<TranslatedTextValue> {

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);
    }

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getNonNullSchema("TranslatedTextValue")).getPrimaryKey().getFieldPaths());
    }

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TranslatedTextValue", isListenToDataRefresh, fieldPaths);
    }

    @Override
    public TranslatedTextValue findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTranslatedTextValue(ordinal);
    }

}