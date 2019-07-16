package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SupplementalSubtype extends HollowObject {

    public SupplementalSubtype(SupplementalSubtypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getValue() {
        return delegate().getValue(ordinal);
    }

    public boolean isValueEqual(String testValue) {
        return delegate().isValueEqual(ordinal, testValue);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public SupplementalSubtypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SupplementalSubtypeDelegate delegate() {
        return (SupplementalSubtypeDelegate)delegate;
    }

}