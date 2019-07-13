package com.netflix.vms.transformer.input.api.gen.cupToken;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class CinderCupTokenRecordDataAccessor extends AbstractHollowDataAccessor<CinderCupTokenRecord> {

    public static final String TYPE = "CinderCupTokenRecord";
    private CupTokenAPI api;

    public CinderCupTokenRecordDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (CupTokenAPI)consumer.getAPI();
    }

    public CinderCupTokenRecordDataAccessor(HollowReadStateEngine rStateEngine, CupTokenAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public CinderCupTokenRecordDataAccessor(HollowReadStateEngine rStateEngine, CupTokenAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public CinderCupTokenRecordDataAccessor(HollowReadStateEngine rStateEngine, CupTokenAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public CinderCupTokenRecord getRecord(int ordinal){
        return api.getCinderCupTokenRecord(ordinal);
    }

}