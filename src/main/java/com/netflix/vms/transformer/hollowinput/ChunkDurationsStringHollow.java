package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ChunkDurationsStringHollow extends HollowObject {

    public ChunkDurationsStringHollow(ChunkDurationsStringDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean _isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ChunkDurationsStringTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ChunkDurationsStringDelegate delegate() {
        return (ChunkDurationsStringDelegate)delegate;
    }

}