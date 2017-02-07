package com.netflix.hollow.api.producer;

import com.netflix.hollow.api.StateTransition;
import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

public class WriteState {

    private final StateTransition transition;
    private final HollowObjectMapper objectMapper;

    WriteState(HollowWriteStateEngine writeEngine, StateTransition transition) {
        this.transition = transition;
        this.objectMapper = new HollowObjectMapper(writeEngine);
    }

    public int add(Object o) {
        return objectMapper.add(o);
    }

    public HollowObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public HollowWriteStateEngine getStateEngine() {
        return objectMapper.getStateEngine();
    }

    long getVersion() {
        return transition.getToVersion();
    }

    StateTransition getTransition() {
        return transition;
    }

}
