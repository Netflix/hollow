package com.netflix.hollow.diff.ui.temp.accessor;

import com.netflix.hollow.diff.ui.temp.MyNamespaceAPI;
import com.netflix.hollow.diff.ui.temp.ProfileId;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ProfileIdDataAccessor extends AbstractHollowDataAccessor<ProfileId> {

    public static final String TYPE = "profileId";
    private MyNamespaceAPI api;

    public ProfileIdDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MyNamespaceAPI)consumer.getAPI();
    }

    public ProfileIdDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ProfileIdDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ProfileIdDataAccessor(HollowReadStateEngine rStateEngine, MyNamespaceAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public ProfileId getRecord(int ordinal){
        return api.getProfileId(ordinal);
    }

}