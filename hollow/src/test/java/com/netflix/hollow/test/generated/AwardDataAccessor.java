package com.netflix.hollow.test.generated;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class AwardDataAccessor extends AbstractHollowDataAccessor<Award> {

    public static final String TYPE = "Award";
    private AwardsAPI api;

    public AwardDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (AwardsAPI)consumer.getAPI();
    }

    public AwardDataAccessor(HollowReadStateEngine rStateEngine, AwardsAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public AwardDataAccessor(HollowReadStateEngine rStateEngine, AwardsAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public AwardDataAccessor(HollowReadStateEngine rStateEngine, AwardsAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Award getRecord(int ordinal){
        return api.getAward(ordinal);
    }

}