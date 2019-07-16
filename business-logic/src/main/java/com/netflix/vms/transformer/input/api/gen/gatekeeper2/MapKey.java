package com.netflix.vms.transformer.input.api.gen.gatekeeper2;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

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

    public Gk2StatusAPI api() {
        return typeApi().getAPI();
    }

    public MapKeyTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MapKeyDelegate delegate() {
        return (MapKeyDelegate)delegate;
    }

}