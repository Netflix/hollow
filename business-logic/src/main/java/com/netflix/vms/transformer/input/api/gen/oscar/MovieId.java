package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieId extends HollowObject {

    public MovieId(MovieIdDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long getValue() {
        return delegate().getValue(ordinal);
    }

    public Long getValueBoxed() {
        return delegate().getValueBoxed(ordinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public MovieIdTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieIdDelegate delegate() {
        return (MovieIdDelegate)delegate;
    }

}