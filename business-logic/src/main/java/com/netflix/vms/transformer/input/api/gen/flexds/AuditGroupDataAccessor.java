package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class AuditGroupDataAccessor extends AbstractHollowDataAccessor<AuditGroup> {

    public static final String TYPE = "AuditGroup";
    private FlexDSAPI api;

    public AuditGroupDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (FlexDSAPI)consumer.getAPI();
    }

    public AuditGroupDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public AuditGroupDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public AuditGroupDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public AuditGroup getRecord(int ordinal){
        return api.getAuditGroup(ordinal);
    }

}