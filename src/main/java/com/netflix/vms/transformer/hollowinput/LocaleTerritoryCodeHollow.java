package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class LocaleTerritoryCodeHollow extends HollowObject {

    public LocaleTerritoryCodeHollow(LocaleTerritoryCodeDelegate delegate, int ordinal) {
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

    public LocaleTerritoryCodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LocaleTerritoryCodeDelegate delegate() {
        return (LocaleTerritoryCodeDelegate)delegate;
    }

}