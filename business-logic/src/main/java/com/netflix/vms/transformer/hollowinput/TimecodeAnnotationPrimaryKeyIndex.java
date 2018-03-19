package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodeAnnotationPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TimecodeAnnotationHollow> {

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, false);    }

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah) {
        this(consumer, isListenToDataRefreah, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TimecodeAnnotation")).getPrimaryKey().getFieldPaths());
    }

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefreah, String... fieldPaths) {
        super(consumer, "TimecodeAnnotation", isListenToDataRefreah, fieldPaths);
    }

    public TimecodeAnnotationHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTimecodeAnnotationHollow(ordinal);
    }

}