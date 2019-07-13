package com.netflix.vms.transformer.input.api.gen.exhibitDealAttribute;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class DisallowedAssetBundleEntryDataAccessor extends AbstractHollowDataAccessor<DisallowedAssetBundleEntry> {

    public static final String TYPE = "DisallowedAssetBundleEntry";
    private ExhibitDealAttributeV1API api;

    public DisallowedAssetBundleEntryDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (ExhibitDealAttributeV1API)consumer.getAPI();
    }

    public DisallowedAssetBundleEntryDataAccessor(HollowReadStateEngine rStateEngine, ExhibitDealAttributeV1API api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public DisallowedAssetBundleEntryDataAccessor(HollowReadStateEngine rStateEngine, ExhibitDealAttributeV1API api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public DisallowedAssetBundleEntryDataAccessor(HollowReadStateEngine rStateEngine, ExhibitDealAttributeV1API api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public DisallowedAssetBundleEntry getRecord(int ordinal){
        return api.getDisallowedAssetBundleEntry(ordinal);
    }

}