package com.netflix.vms.transformer.input.api.gen.flexds;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

@SuppressWarnings("all")
public class ContainerDataAccessor extends AbstractHollowDataAccessor<Container> {

    public static final String TYPE = "Container";
    private FlexDSAPI api;

    public ContainerDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
        this.api = (FlexDSAPI)consumer.getAPI();
    }

    public ContainerDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api) {
        super(rStateEngine, TYPE);
        this.api = api;
    }

    public ContainerDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
        this.api = api;
    }

    public ContainerDataAccessor(HollowReadStateEngine rStateEngine, FlexDSAPI api, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
        this.api = api;
    }

    @Override public Container getRecord(int ordinal){
        return api.getContainer(ordinal);
    }

}