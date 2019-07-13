package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class TranslatedTextValueDataAccessor extends AbstractHollowDataAccessor<TranslatedTextValue> {

    public static final String TYPE = "TranslatedTextValue";
    private LocalizedMetaDataAPI api;

    public TranslatedTextValueDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (LocalizedMetaDataAPI)consumer.getAPI();
    }

    public TranslatedTextValueDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public TranslatedTextValueDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public TranslatedTextValueDataAccessor(HollowReadStateEngine rStateEngine, LocalizedMetaDataAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public TranslatedTextValue getRecord(int ordinal){
        return api.getTranslatedTextValue(ordinal);
    }

}