package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class ShowCountryLabelHollow extends HollowObject {

    public ShowCountryLabelHollow(ShowCountryLabelDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getVideoId() {
        return delegate().getVideoId(ordinal);
    }

    public Long _getVideoIdBoxed() {
        return delegate().getVideoIdBoxed(ordinal);
    }

    public ShowMemberTypeListHollow _getShowMemberTypes() {
        int refOrdinal = delegate().getShowMemberTypesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getShowMemberTypeListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public ShowCountryLabelTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ShowCountryLabelDelegate delegate() {
        return (ShowCountryLabelDelegate)delegate;
    }

}