package com.netflix.vms.transformer.common.input;

import com.netflix.hollow.api.custom.HollowAPI;

public abstract class UpstreamDataset {

    protected final InputState inputState;

    protected UpstreamDataset(InputState inputState) {
        this.inputState = inputState;
    }

    public InputState getInputState() {
        return inputState;
    }

    public abstract HollowAPI getAPI();

}