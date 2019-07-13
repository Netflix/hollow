package com.netflix.vms.transformer.input.api.gen.packageDealCountry;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class PackageMovieDealCountryGroupDataAccessor extends AbstractHollowDataAccessor<PackageMovieDealCountryGroup> {

    public static final String TYPE = "PackageMovieDealCountryGroup";
    private PackageDealCountryAPI api;

    public PackageMovieDealCountryGroupDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (PackageDealCountryAPI)consumer.getAPI();
    }

    public PackageMovieDealCountryGroupDataAccessor(HollowReadStateEngine rStateEngine, PackageDealCountryAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public PackageMovieDealCountryGroupDataAccessor(HollowReadStateEngine rStateEngine, PackageDealCountryAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public PackageMovieDealCountryGroupDataAccessor(HollowReadStateEngine rStateEngine, PackageDealCountryAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public PackageMovieDealCountryGroup getRecord(int ordinal){
        return api.getPackageMovieDealCountryGroup(ordinal);
    }

}