package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.HollowObject;
import com.netflix.hollow.core.schema.HollowObjectSchema;

import com.netflix.hollow.tools.stringifier.HollowRecordStringifier;

@SuppressWarnings("all")
public class DamMerchStillsMomentHollow extends HollowObject {

    public DamMerchStillsMomentHollow(DamMerchStillsMomentDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getPackageId() {
        int refOrdinal = delegate().getPackageIdOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public StringHollow _getStillTS() {
        int refOrdinal = delegate().getStillTSOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public DamMerchStillsMomentTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected DamMerchStillsMomentDelegate delegate() {
        return (DamMerchStillsMomentDelegate)delegate;
    }

}