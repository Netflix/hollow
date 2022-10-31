package com.netflix.hollow.diff.ui.temp.accessor;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.MyEntity;
import com.netflix.hollow.diff.ui.temp.core.*;
import com.netflix.hollow.diff.ui.temp.collections.*;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class MyEntityDataAccessor extends AbstractHollowDataAccessor<MyEntity> {

    public static final String TYPE = "MyEntity";
    private MyNamespaceAPI api;

    public MyEntityDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MyNamespaceAPI)consumer.getAPI();
    }

    public MyEntityDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public MyEntityDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public MyEntityDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public MyEntity getRecord(int ordinal){
        return api.getMyEntity(ordinal);
    }

}