package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class DamMerchStillsHollow extends HollowObject {

    public DamMerchStillsHollow(DamMerchStillsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getAssetId() {
        int refOrdinal = delegate().getAssetIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public DamMerchStillsMomentHollow _getMoment() {
        int refOrdinal = delegate().getMomentOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getDamMerchStillsMomentHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DamMerchStillsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DamMerchStillsDelegate delegate() {
        return (DamMerchStillsDelegate)delegate;
    }

}