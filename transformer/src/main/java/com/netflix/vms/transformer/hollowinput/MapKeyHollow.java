package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MapKeyHollow extends HollowObject {

    public MapKeyHollow(MapKeyDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean _isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public MapKeyTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MapKeyDelegate delegate() {
        return (MapKeyDelegate)delegate;
    }

}