package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.consumer.data.AbstractHollowDataAccessor;
import com.netflix.hollow.core.index.key.PrimaryKey;
import com.netflix.hollow.core.read.engine.HollowReadStateEngine;

public class CodecPrivateDataStringDataAccessor extends AbstractHollowDataAccessor<CodecPrivateDataStringHollow> {

    public static final String TYPE = "CodecPrivateDataStringHollow";
    private VMSHollowInputAPI api;

    public CodecPrivateDataStringDataAccessor(HollowConsumer consumer) {
        super(consumer, TYPE);
    }

    public CodecPrivateDataStringDataAccessor(HollowReadStateEngine rStateEngine) {
        super(rStateEngine, TYPE);
    }

    public CodecPrivateDataStringDataAccessor(HollowReadStateEngine rStateEngine, String ... fieldPaths) {
        super(rStateEngine, TYPE, fieldPaths);
    }

    public CodecPrivateDataStringDataAccessor(HollowReadStateEngine rStateEngine, PrimaryKey primaryKey) {
        super(rStateEngine, TYPE, primaryKey);
    }

    @Override public CodecPrivateDataStringHollow getRecord(int ordinal){
        return api.getCodecPrivateDataStringHollow(ordinal);
    }

}