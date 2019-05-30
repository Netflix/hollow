package com.netflix.vms.transformer.common.input;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class InputState {
    private final HollowReadStateEngine stateEngine;
    private final long version;

    public InputState(HollowConsumer consumer) {
        this(consumer.getStateEngine(), consumer.getCurrentVersionId());
    }

    public InputState(HollowReadStateEngine stateEngine, long version) {
        this.stateEngine = stateEngine;
        this.version = version;
    }

    public HollowReadStateEngine getStateEngine() {
        return stateEngine;
    }

    public long getVersion() {
        return version;
    }

}