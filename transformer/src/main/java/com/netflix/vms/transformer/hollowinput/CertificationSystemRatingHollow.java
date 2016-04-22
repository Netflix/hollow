package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationSystemRatingHollow extends HollowObject {

    public CertificationSystemRatingHollow(CertificationSystemRatingDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getRatingCode() {
        int refOrdinal = delegate().getRatingCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public long _getRatingId() {
        return delegate().getRatingId(ordinal);
    }

    public Long _getRatingIdBoxed() {
        return delegate().getRatingIdBoxed(ordinal);
    }

    public long _getMaturityLevel() {
        return delegate().getMaturityLevel(ordinal);
    }

    public Long _getMaturityLevelBoxed() {
        return delegate().getMaturityLevelBoxed(ordinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationSystemRatingTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CertificationSystemRatingDelegate delegate() {
        return (CertificationSystemRatingDelegate)delegate;
    }

}