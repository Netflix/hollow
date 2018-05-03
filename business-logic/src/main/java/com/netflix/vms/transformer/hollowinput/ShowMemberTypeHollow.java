package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class ShowMemberTypeHollow extends HollowObject {

    public ShowMemberTypeHollow(ShowMemberTypeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ISOCountryListHollow _getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryListHollow(refOrdinal);
    }

    public long _getSequenceLabelId() {
        return delegate().getSequenceLabelId(ordinal);
    }

    public Long _getSequenceLabelIdBoxed() {
        return delegate().getSequenceLabelIdBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowMemberTypeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowMemberTypeDelegate delegate() {
        return (ShowMemberTypeDelegate)delegate;
    }

}