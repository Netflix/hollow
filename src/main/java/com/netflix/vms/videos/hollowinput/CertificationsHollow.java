package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationsHollow extends HollowObject {

    public CertificationsHollow(CertificationsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CertificationsNameHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCertificationsNameHollow(refOrdinal);
    }

    public CertificationsDescriptionHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCertificationsDescriptionHollow(refOrdinal);
    }

    public long _getCertificationTypeId() {
        return delegate().getCertificationTypeId(ordinal);
    }

    public Long _getCertificationTypeIdBoxed() {
        return delegate().getCertificationTypeIdBoxed(ordinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CertificationsDelegate delegate() {
        return (CertificationsDelegate)delegate;
    }

}