package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class MapKeyHollow extends HollowObject {

    public MapKeyHollow(MapKeyDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String _getKey() {
        return delegate().getKey(ordinal);
    }

    public boolean _isKeyEqual(String testValue) {
        return delegate().isKeyEqual(ordinal, testValue);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public MapKeyTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MapKeyDelegate delegate() {
        return (MapKeyDelegate)delegate;
    }

}