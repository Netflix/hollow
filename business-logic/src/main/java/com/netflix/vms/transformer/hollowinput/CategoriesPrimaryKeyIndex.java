package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.index.AbstractHollowUniqueKeyIndex;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class CategoriesPrimaryKeyIndex extends AbstractHollowUniqueKeyIndex<VMSHollowInputAPI, CategoriesHollow> {

    public CategoriesPrimaryKeyIndex(HollowConsumer consumer) {
        this(consumer, true);    }

    public CategoriesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh) {
        this(consumer, isListenToDataRefresh, ((HollowObjectSchema)consumer.getStateEngine().getSchema("Categories")).getPrimaryKey().getFieldPaths());
    }

    public CategoriesPrimaryKeyIndex(HollowConsumer consumer, String... fieldPaths) {
        this(consumer, true, fieldPaths);
    }

    public CategoriesPrimaryKeyIndex(HollowConsumer consumer, boolean isListenToDataRefresh, String... fieldPaths) {
        super(consumer, "Categories", isListenToDataRefresh, fieldPaths);
    }

    public CategoriesHollow findMatch(Object... keys) {
        int ordinal = idx.getMatchingOrdinal(keys);
        if(ordinal == -1)
            return null;
        return api.getCategoriesHollow(ordinal);
    }

}