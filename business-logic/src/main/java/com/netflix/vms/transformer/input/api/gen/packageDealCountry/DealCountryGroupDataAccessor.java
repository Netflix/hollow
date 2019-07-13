package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class DealCountryGroupDataAccessor extends AbstractHollowDataAccessor<DealCountryGroup> {

    public static final String TYPE = "DealCountryGroup";
    private PackageDealCountryAPI api;

    public DealCountryGroupDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (PackageDealCountryAPI)consumer.getAPI();
    }

    public DealCountryGroupDataAccessor(HollowReadStateEngine rStateEngine, PackageDealCountryAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public DealCountryGroupDataAccessor(HollowReadStateEngine rStateEngine, PackageDealCountryAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public DealCountryGroupDataAccessor(HollowReadStateEngine rStateEngine, PackageDealCountryAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public DealCountryGroup getRecord(int ordinal){
        return api.getDealCountryGroup(ordinal);
    }

}