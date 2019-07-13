package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class LocalizedMetadataDataAccessor extends AbstractHollowDataAccessor<LocalizedMetadata> {

    public static final String TYPE = "LocalizedMetadata";
    private LocalizedMetaDataAPI api;

    public LocalizedMetadataDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (LocalizedMetaDataAPI)consumer.getAPI();
    }

    public LocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public LocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public LocalizedMetadataDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public LocalizedMetadata getRecord(int ordinal){
        return api.getLocalizedMetadata(ordinal);
    }

}