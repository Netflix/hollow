package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CinderCupTokenRecordPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CinderCupTokenRecordHollow> {

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CinderCupTokenRecord")).getPrimaryKey().getFieldPaths());
    }

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CinderCupTokenRecordPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CinderCupTokenRecord", isListenToDataRefresh, fieldPaths);
    }

    public CinderCupTokenRecordHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCinderCupTokenRecordHollow(ordinal);
    }

}