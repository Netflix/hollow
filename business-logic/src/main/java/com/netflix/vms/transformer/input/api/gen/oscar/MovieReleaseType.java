package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieReleaseType extends HollowObject {

    public MovieReleaseType(MovieReleaseTypeDelegate delegate, int ordinal) {
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

    public MovieReleaseTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieReleaseTypeDelegate delegate() {
        return (MovieReleaseTypeDelegate)delegate;
    }

}