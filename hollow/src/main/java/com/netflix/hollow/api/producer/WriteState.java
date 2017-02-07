package com.netflix.hollow.api.producer;

import com.netflix.hollow.core.write.HollowWriteStateEngine;
import com.netflix.hollow.core.write.objectmapper.HollowObjectMapper;

public class WriteState {
    private final long version;
    private final HollowObjectMapper objectMapper;

    WriteState(HollowWriteStateEngine writeEngine, long version) {
        this.objectMapper = new HollowObjectMapper(writeEngine);
        this.version = version;
    }

    public long getVersion() {
        return version;
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
}
