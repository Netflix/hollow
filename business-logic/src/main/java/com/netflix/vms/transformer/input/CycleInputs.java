package com.netflix.vms.transformer.input;

import com.netflix.vms.transformer.common.input.InputState;
import java.util.Map;

public class CycleInputs {
    private final Map<UpstreamDatasetHolder.Dataset, InputState> inputs;
    private final long cycleNumber;

    public CycleInputs(Map<UpstreamDatasetHolder.Dataset, InputState> inputs, long cycleNumber) {
        this.inputs = inputs;
        this.cycleNumber = cycleNumber;
    }

    public Map<UpstreamDatasetHolder.Dataset, InputState> getInputs() {
        return inputs;
    }

    public long getCycleNumber() {
        return cycleNumber;
    }
}
