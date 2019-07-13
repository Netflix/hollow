package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class DerivativeTagDataAccessor extends AbstractHollowDataAccessor<DerivativeTag> {

    public static final String TYPE = "DerivativeTag";
    private MceImageV3API api;

    public DerivativeTagDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MceImageV3API)consumer.getAPI();
    }

    public DerivativeTagDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public DerivativeTagDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public DerivativeTagDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public DerivativeTag getRecord(int ordinal){
        return api.getDerivativeTag(ordinal);
    }

}