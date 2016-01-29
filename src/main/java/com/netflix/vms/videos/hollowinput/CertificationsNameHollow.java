package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class CertificationsNameHollow extends HollowObject {

    public CertificationsNameHollow(CertificationsNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public CertificationsNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getCertificationsNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public CertificationsNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected CertificationsNameDelegate delegate() {
        return (CertificationsNameDelegate)delegate;
    }

}