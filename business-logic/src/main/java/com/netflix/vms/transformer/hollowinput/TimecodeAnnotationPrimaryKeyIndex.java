package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class TimecodeAnnotationPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, TimecodeAnnotationHollow> {

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("TimecodeAnnotation")).getPrimaryKey().getFieldPaths());
    }

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public TimecodeAnnotationPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "TimecodeAnnotation", isListenToDataRefresh, fieldPaths);
    }

    public TimecodeAnnotationHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getTimecodeAnnotationHollow(ordinal);
    }

}