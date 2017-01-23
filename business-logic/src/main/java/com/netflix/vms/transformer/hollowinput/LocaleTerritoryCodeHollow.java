package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
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

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public LocaleTerritoryCodeTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected LocaleTerritoryCodeDelegate delegate() {
        return (LocaleTerritoryCodeDelegate)delegate;
    }

}