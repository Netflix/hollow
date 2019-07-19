package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class IsOriginalTitle extends HollowObject {

    public IsOriginalTitle(IsOriginalTitleDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean getValue() {
        return delegate().getValue(ordinal);
    }

    public Boolean getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public IsOriginalTitleTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected IsOriginalTitleDelegate delegate() {
        return (IsOriginalTitleDelegate)delegate;
    }

}