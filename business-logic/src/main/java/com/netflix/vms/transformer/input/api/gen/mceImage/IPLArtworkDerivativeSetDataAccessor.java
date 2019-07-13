package com.netflix.vms.transformer.input.api.gen.mceImage;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class IPLArtworkDerivativeSetDataAccessor extends AbstractHollowDataAccessor<IPLArtworkDerivativeSet> {

    public static final String TYPE = "IPLArtworkDerivativeSet";
    private MceImageV3API api;

    public IPLArtworkDerivativeSetDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (MceImageV3API)consumer.getAPI();
    }

    public IPLArtworkDerivativeSetDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public IPLArtworkDerivativeSetDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public IPLArtworkDerivativeSetDataAccessor(HollowReadStateEngine rStateEngine, MceImageV3API api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public IPLArtworkDerivativeSet getRecord(int ordinal){
        return api.getIPLArtworkDerivativeSet(ordinal);
    }

}