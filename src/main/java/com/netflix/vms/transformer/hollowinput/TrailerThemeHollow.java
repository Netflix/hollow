package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class TrailerThemeHollow extends HollowObject {

    public TrailerThemeHollow(TrailerThemeDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getValue() {
        int refOrdinal = delegate().getValueOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public TrailerThemeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected TrailerThemeDelegate delegate() {
        return (TrailerThemeDelegate)delegate;
    }

}