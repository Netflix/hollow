package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class MapKeyDataAccessor extends AbstractHollowDataAccessor<MapKey> {

    public static final String TYPE = "MapKey";
    private LocalizedMetaDataAPI api;

    public MapKeyDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (LocalizedMetaDataAPI)consumer.getAPI();
    }

    public MapKeyDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public MapKeyDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public MapKeyDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public MapKey getRecord(int ordinal){
        return api.getMapKey(ordinal);
    }

}