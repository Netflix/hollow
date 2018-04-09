package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CategoryGroupsPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CategoryGroupsHollow> {

    public CategoryGroupsPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CategoryGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("CategoryGroups")).getPrimaryKey().getFieldPaths());
    }

    public CategoryGroupsPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CategoryGroupsPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "CategoryGroups", isListenToDataRefresh, fieldPaths);
    }

    public CategoryGroupsHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCategoryGroupsHollow(ordinal);
    }

}