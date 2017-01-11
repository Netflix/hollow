package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class TopNAttributeHollow extends HollowObject {

    public TopNAttributeHollow(TopNAttributeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCountry() {
        int refOrdinal = delegate().getCountryOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getViewShare() {
        int refOrdinal = delegate().getViewShareOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getCountryViewHrs() {
        int refOrdinal = delegate().getCountryViewHrsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TopNAttributeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TopNAttributeDelegate delegate() {
        return (TopNAttributeDelegate)delegate;
    }

}