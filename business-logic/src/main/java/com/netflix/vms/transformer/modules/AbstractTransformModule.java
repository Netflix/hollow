package com.netflix.vms.transformer.modules;

import com.netflix.hollow.write.objectmapper.HollowObjectMapper;
import com.netflix.vms.transformer.common.TransformerContext;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

public abstract class AbstractTransformModule implements TransformModule {
    protected final VMSHollowInputAPI api;
    protected final HollowObjectMapper mapper;
    protected final TransformerContext ctx;
    protected String name;

    public AbstractTransformModule(VMSHollowInputAPI api, TransformerContext ctx, HollowObjectMapper mapper) {
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