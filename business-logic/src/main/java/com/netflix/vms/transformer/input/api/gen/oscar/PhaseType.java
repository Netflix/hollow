package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class PhaseType extends HollowObject {

    public PhaseType(PhaseTypeDelegate delegate, int ordinal) {
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

    public PhaseTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected PhaseTypeDelegate delegate() {
        return (PhaseTypeDelegate)delegate;
    }

}