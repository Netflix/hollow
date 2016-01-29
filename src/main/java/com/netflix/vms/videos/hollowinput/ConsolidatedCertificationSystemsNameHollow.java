package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsNameHollow extends HollowObject {

    public ConsolidatedCertificationSystemsNameHollow(ConsolidatedCertificationSystemsNameDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsNameMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsNameTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsNameDelegate delegate() {
        return (ConsolidatedCertificationSystemsNameDelegate)delegate;
    }

}