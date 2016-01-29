package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationsDescriptionHollow extends HollowObject {

    public CertificationsDescriptionHollow(CertificationsDescriptionDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CertificationsDescriptionMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCertificationsDescriptionMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationsDescriptionTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CertificationsDescriptionDelegate delegate() {
        return (CertificationsDescriptionDelegate)delegate;
    }

}