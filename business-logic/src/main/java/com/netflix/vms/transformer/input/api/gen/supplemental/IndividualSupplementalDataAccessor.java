package com.netflix.vms.transformer.input.api.gen.supplemental;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class IndividualSupplementalDataAccessor extends AbstractHollowDataAccessor<IndividualSupplemental> {

    public static final String TYPE = "IndividualSupplemental";
    private SupplementalAPI api;

    public IndividualSupplementalDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (SupplementalAPI)consumer.getAPI();
    }

    public IndividualSupplementalDataAccessor(HollowReadStateEngine rStateEngine, SupplementalAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public IndividualSupplementalDataAccessor(HollowReadStateEngine rStateEngine, SupplementalAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public IndividualSupplementalDataAccessor(HollowReadStateEngine rStateEngine, SupplementalAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public IndividualSupplemental getRecord(int ordinal){
        return api.getIndividualSupplemental(ordinal);
    }

}