package com.netflix.vms.transformer.input.api.gen.oscar;

import com.netflix.hollow.api.consumer.HollowConsumer;
import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowMemberType extends HollowObject {

    public ShowMemberType(ShowMemberTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ISOCountryList getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryList(refOrdinal);
    }

    public long getSequenceLabelId() {
        return delegate().getSequenceLabelId(ordinal);
    }

    public Long getSequenceLabelIdBoxed() {
        return delegate().getSequenceLabelIdBoxed(ordinal);
    }

    public OscarAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowMemberTypeDelegate delegate() {
        return (ShowMemberTypeDelegate)delegate;
    }

}