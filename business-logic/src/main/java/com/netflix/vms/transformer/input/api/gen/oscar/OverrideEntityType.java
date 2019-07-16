package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class OverrideEntityType extends HollowObject {

    public OverrideEntityType(OverrideEntityTypeDelegate delegate, int ordinal) {
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

    public OverrideEntityTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected OverrideEntityTypeDelegate delegate() {
        return (OverrideEntityTypeDelegate)delegate;
    }

}