package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationsHollow extends HollowObject {

    public CertificationsHollow(CertificationsDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public long _getCertificationTypeId() {
        return delegate().getCertificationTypeId(ordinal);
    }

    public Long _getCertificationTypeIdBoxed() {
        return delegate().getCertificationTypeIdBoxed(ordinal);
    }

    public TranslatedTextHollow _getName() {
        int refOrdinal = delegate().getNameOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public TranslatedTextHollow _getDescription() {
        int refOrdinal = delegate().getDescriptionOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getTranslatedTextHollow(refOrdinal);
    }

    public VMSHollowInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationsTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CertificationsDelegate delegate() {
        return (CertificationsDelegate)delegate;
    }

}