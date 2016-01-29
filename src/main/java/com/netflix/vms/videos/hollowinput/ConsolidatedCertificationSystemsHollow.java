package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsHollow extends HollowObject {

    public ConsolidatedCertificationSystemsHollow(ConsolidatedCertificationSystemsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public StringHollow _getCountryCode() {
        int refOrdinal = delegate().getCountryCodeOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public ConsolidatedCertificationSystemsArrayOfRatingHollow _getRating() {
        int refOrdinal = delegate().getRatingOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsArrayOfRatingHollow(refOrdinal);
    }

    public ConsolidatedCertificationSystemsNameHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsNameHollow(refOrdinal);
    }

    public long _getCertificationSystemId() {
        return delegate().getCertificationSystemId(ordinal);
    }

    public Long _getCertificationSystemIdBoxed() {
        return delegate().getCertificationSystemIdBoxed(ordinal);
    }

    public ConsolidatedCertificationSystemsDescriptionHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsDescriptionHollow(refOrdinal);
    }

    public StringHollow _getOfficialURL() {
        int refOrdinal = delegate().getOfficialURLOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getStringHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsDelegate delegate() {
        return (ConsolidatedCertificationSystemsDelegate)delegate;
    }

}