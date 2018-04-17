package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodedMomentAnnotationPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TimecodedMomentAnnotationHollow> {

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TimecodedMomentAnnotation")).getPrimaryKey().getFieldPaths());
    }

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TimecodedMomentAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TimecodedMomentAnnotation", isListenToDataRefresh, fieldPaths);
    }

    public TimecodedMomentAnnotationHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTimecodedMomentAnnotationHollow(ordinal);
    }

}