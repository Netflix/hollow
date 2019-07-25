package com.netflix.vms.transformer.input.datasets;

import com.netflix.hollow.core.read.engine.HollowReadStateEngine;
import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.input.api.gen.topn.TopNAPI;

public class TopNDataset extends UpstreamDataset {
    private final TopNAPI api;

    public TopNDataset(InputState input) {
        super(input);
        HollowReadStateEngine readStateEngine = input.getStateEngine();
        this.api = new TopNAPI(readStateEngine);
    }

    @Override
    public TopNAPI getAPI() {
        return api;
    }
}
