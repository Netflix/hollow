package com.netflix.vms.transformer.common.input;

import static com.netflix.vms.transformer.common.input.UpstreamDatasetDefinition.DatasetIdentifier;

import java.util.Map;

public class CycleInputs {
    private final Map<DatasetIdentifier, InputState> inputs;
    private final long cycleNumber;

    public CycleInputs(Map<DatasetIdentifier, InputState> inputs, long cycleNumber) {
        this.inputs = inputs;
        this.cycleNumber = cycleNumber;
    }

    public Map<DatasetIdentifier, InputState> getInputs() {
        return inputs;
    }

    public long getCycleNumber() {
        return cycleNumber;
    }
}
