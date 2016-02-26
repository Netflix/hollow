package com.netflix.vms.transformer.modules;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;

public abstract class AbstractTransformModule implements TransformModule {
    protected final VMSHollowVideoInputAPI api;
    protected final HollowObjectMapper mapper;
    protected String name;

    public AbstractTransformModule(VMSHollowVideoInputAPI api, HollowObjectMapper mapper) {
        this.api = api;
        this.mapper = mapper;
        this.name = this.getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return this.name;
    }
}