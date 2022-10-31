package com.netflix.hollow.diff.ui.temp.accessor;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.MyEntityRankIndex;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class MyEntityRankIndexDataAccessor extends AbstractHollowDataAccessor<MyEntityRankIndex> {

    public static final String TYPE = "MyEntityRankIndex";
    private MyNamespaceAPI api;

    public MyEntityRankIndexDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MyNamespaceAPI)consumer.getAPI();
    }

    public MyEntityRankIndexDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public MyEntityRankIndexDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public MyEntityRankIndexDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public MyEntityRankIndex getRecord(int ordinal){
        return api.getMyEntityRankIndex(ordinal);
    }

}