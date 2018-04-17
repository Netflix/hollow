package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class DisallowedSubtitleLangCodePrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, DisallowedSubtitleLangCodeHollow> {

    public DisallowedSubtitleLangCodePrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public DisallowedSubtitleLangCodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("DisallowedSubtitleLangCode")).getPrimaryKey().getFieldPaths());
    }

    public DisallowedSubtitleLangCodePrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public DisallowedSubtitleLangCodePrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "DisallowedSubtitleLangCode", isListenToDataRefresh, fieldPaths);
    }

    public DisallowedSubtitleLangCodeHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getDisallowedSubtitleLangCodeHollow(ordinal);
    }

}