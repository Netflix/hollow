package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class TerritoryCountriesHollow extends HollowObject {

    public TerritoryCountriesHollow(TerritoryCountriesDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getTerritoryCode() {
        int refOrdinal = delegate().getTerritoryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ISOCountryListHollow _getCountryCodes() {
        int refOrdinal = delegate().getCountryCodesOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getISOCountryListHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public TerritoryCountriesTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TerritoryCountriesDelegate delegate() {
        return (TerritoryCountriesDelegate)delegate;
    }

}