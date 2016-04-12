package com.netflix.vms.transformer.modules;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowVideoInputAPI;

public abstract class AbstractTransformModule implements TransformModule {
    protected final VMSHollowVideoInputAPI api;
    protected final HollowObjectMapper mapper;
    protected final TransformerContext ctx;
    protected String name;

    public AbstractTransformModule(VMSHollowVideoInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
        this.api = api;
        this.ctx = ctx;
        this.mapper = mapper;
        this.name = this.getClass().getSimpleName();
    }

    @Override
    public String getName() {
        return this.name;
    }
}