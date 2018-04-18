package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;

@SuppressWarnings("all")
public class DashStreamHeaderDataHollow extends HollowObject {

    public DashStreamHeaderDataHollow(DashStreamHeaderDataDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public SetOfStreamBoxInfoHollow _getBoxInfo() {
        int refOrdinal = delegate().getBoxInfoOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getSetOfStreamBoxInfoHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DashStreamHeaderDataTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DashStreamHeaderDataDelegate delegate() {
        return (DashStreamHeaderDataDelegate)delegate;
    }

}