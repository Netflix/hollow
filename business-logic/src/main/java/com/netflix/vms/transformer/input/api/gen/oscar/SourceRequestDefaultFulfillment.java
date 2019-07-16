package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class SourceRequestDefaultFulfillment extends HollowObject {

    public SourceRequestDefaultFulfillment(SourceRequestDefaultFulfillmentDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String get_name() {
        return delegate().get_name(ordinal);
    }

    public boolean is_nameEqual(String testValue) {
        return delegate().is_nameEqual(ordinal, testValue);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public SourceRequestDefaultFulfillmentTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected SourceRequestDefaultFulfillmentDelegate delegate() {
        return (SourceRequestDefaultFulfillmentDelegate)delegate;
    }

}