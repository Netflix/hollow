package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TranslatedTextValuePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TranslatedTextValueHollow> {

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TranslatedTextValue")).getPrimaryKey().getFieldPaths());
    }

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TranslatedTextValuePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TranslatedTextValue", isListenToDataRefresh, fieldPaths);
    }

    public TranslatedTextValueHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTranslatedTextValueHollow(ordinal);
    }

}