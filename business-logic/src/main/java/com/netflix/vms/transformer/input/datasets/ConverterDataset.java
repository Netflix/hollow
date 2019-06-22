package com.netflix.vms.transformer.input.datasets;

import com.netflix.vms.transformer.common.input.InputState;
import com.netflix.vms.transformer.common.input.UpstreamDataset;
import com.netflix.vms.transformer.hollowinput.VMSHollowInputAPI;

public class ConverterDataset extends UpstreamDataset {

    private final VMSHollowInputAPI api;

    public ConverterDataset(InputState input) {
        super(input);
        this.api = new VMSHollowInputAPI(input.getStateEngine());
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return api;
    }
}
