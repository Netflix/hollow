package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CodecPrivateDataStringPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CodecPrivateDataStringHollow> {

    public CodecPrivateDataStringPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CodecPrivateDataStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CodecPrivateDataString")).getPrimaryKey().getFieldPaths());
    }

    public CodecPrivateDataStringPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CodecPrivateDataStringPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CodecPrivateDataString", isListenToDataRefresh, fieldPaths);
    }

    public CodecPrivateDataStringHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCodecPrivateDataStringHollow(ordinal);
    }

}