package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class MovieSetContentLabel extends HollowObject {

    public MovieSetContentLabel(MovieSetContentLabelDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public String getDescription() {
        return delegate().getDescription(ordinal);
    }

    public boolean isDescriptionEqual(String testValue) {
        return delegate().isDescriptionEqual(ordinal, testValue);
    }

    public HString getDescriptionHollowReference() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getHString(refOrdinal);
    }

    public int getId() {
        return delegate().getId(ordinal);
    }

    public Integer getIdBoxed() {
        return delegate().getIdBoxed(ordinal);
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

    public MovieSetContentLabelTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected MovieSetContentLabelDelegate delegate() {
        return (MovieSetContentLabelDelegate)delegate;
    }

}