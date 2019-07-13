package com.netflix.vms.transformer.input.api.gen.localizedMetaData;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class MapKey extends HollowObject {

    public MapKey(MapKeyDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public LocalizedMetaDataAPI api() {
        return typeApi().getAPI();
    }

    public MapKeyTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MapKeyDelegate delegate() {
        return (MapKeyDelegate)delegate;
    }

}