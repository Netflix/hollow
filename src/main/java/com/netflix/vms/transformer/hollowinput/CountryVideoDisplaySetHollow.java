package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CountryVideoDisplaySetHollow extends HollowObject {

    public CountryVideoDisplaySetHollow(CountryVideoDisplaySetDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getSetType() {
        int refOrdinal = delegate().getSetTypeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public SeasonListHollow _getChildren() {
        int refOrdinal = delegate().getChildrenOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSeasonListHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CountryVideoDisplaySetTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CountryVideoDisplaySetDelegate delegate() {
        return (CountryVideoDisplaySetDelegate)delegate;
    }

}