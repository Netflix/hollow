package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IndividualSupplementalPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, IndividualSupplementalHollow> {

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, ((HollowObjectSchema)consumer.getStateEngine().getSchema("IndividualSupplemental")).getPrimaryKey().getFieldPaths());
    }

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public IndividualSupplementalPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "IndividualSupplemental", isListenToDataRefreah, fieldPaths);
    }

    public IndividualSupplementalHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getIndividualSupplementalHollow(ordinal);
    }

}