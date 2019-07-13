package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class IPLDerivativeGroupDataAccessor extends AbstractHollowDataAccessor<IPLDerivativeGroup> {

    public static final String TYPE = "IPLDerivativeGroup";
    private MceImageV3API api;

    public IPLDerivativeGroupDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MceImageV3API)consumer.getAPI();
    }

    public IPLDerivativeGroupDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public IPLDerivativeGroupDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public IPLDerivativeGroupDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public IPLDerivativeGroup getRecord(int ordinal){
        return api.getIPLDerivativeGroup(ordinal);
    }

}