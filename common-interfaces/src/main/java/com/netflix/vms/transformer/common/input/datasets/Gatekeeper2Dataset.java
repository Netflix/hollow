package com.netflix.vms.transformer.common.input.datasets;

import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

public class Gatekeeper2Dataset extends UpstreamDataset {

    private final VMSHollowInputAPI api;

    public Gatekeeper2Dataset(InputState input) {
        super(input);
        this.api = new VMSHollowInputAPI(input.getStateEngine());
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return api;
    }
}
