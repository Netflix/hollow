package com.netflix.vms.transformer.common.input;

import java.util.Map;

public class CycleInputs {
    private final Map<String, InputState> inputs;
    private final long cycleNumber;

    public CycleInputs(Map<String, InputState> inputs, long cycleNumber) {
        this.inputs = inputs;
        this.cycleNumber = cycleNumber;
    }

    public Map<String, InputState> getInputs() {
        return inputs;
    }

    public long getCycleNumber() {
        return cycleNumber;
    }
}
