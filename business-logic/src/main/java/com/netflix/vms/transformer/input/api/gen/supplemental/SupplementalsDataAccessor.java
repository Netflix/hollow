package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class SupplementalsDataAccessor extends AbstractHollowDataAccessor<Supplementals> {

    public static final String TYPE = "Supplementals";
    private SupplementalAPI api;

    public SupplementalsDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (SupplementalAPI)consumer.getAPI();
    }

    public SupplementalsDataAccessor(HollowReadStateEngine rStateEngine, SupplementalAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public SupplementalsDataAccessor(HollowReadStateEngine rStateEngine, SupplementalAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public SupplementalsDataAccessor(HollowReadStateEngine rStateEngine, SupplementalAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Supplementals getRecord(int ordinal){
        return api.getSupplementals(ordinal);
    }

}