package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieType extends HollowObject {

    public MovieType(MovieTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public boolean getStreamingType() {
        return delegate().getStreamingType(ordinal);
    }

    public Boolean getStreamingTypeBoxed() {
        return delegate().getStreamingTypeBoxed(ordinal);
    }

    public boolean getViewable() {
        return delegate().getViewable(ordinal);
    }

    public Boolean getViewableBoxed() {
        return delegate().getViewableBoxed(ordinal);
    }

    public boolean getMerchable() {
        return delegate().getMerchable(ordinal);
    }

    public Boolean getMerchableBoxed() {
        return delegate().getMerchableBoxed(ordinal);
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

    public MovieTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieTypeDelegate delegate() {
        return (MovieTypeDelegate)delegate;
    }

}