package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TimecodedMomentAnnotationHollow> {

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TimecodedMomentAnnotation")).getPrimaryKey().getFieldPaths());
    }

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "TimecodedMomentAnnotation", isListenToDataRefreah, fieldPaths);
    }

    public TimecodedMomentAnnotationHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTimecodedMomentAnnotationHollow(ordinal);
    }

}