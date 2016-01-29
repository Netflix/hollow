package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.HollowObject;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsDescriptionHollow extends HollowObject {

    public ConsolidatedCertificationSystemsDescriptionHollow(ConsolidatedCertificationSystemsDescriptionDelegate delegate, int ordinal) {
        super(delegate, ordinal);
    }

    public ConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow _getTranslatedTexts() {
        int refOrdinal = delegate().getTranslatedTextsOrdinal(ordinal);
        if(refOrdinal == -1)
            return null;
        return  api().getConsolidatedCertificationSystemsDescriptionMapOfTranslatedTextsHollow(refOrdinal);
    }

    public VMSHollowVideoInputAPI api() {
        return typeApi().getAPI();
    }

    public ConsolidatedCertificationSystemsDescriptionTypeAPI typeApi() {
        return delegate().getTypeAPI();
    }

    protected ConsolidatedCertificationSystemsDescriptionDelegate delegate() {
        return (ConsolidatedCertificationSystemsDescriptionDelegate)delegate;
    }

}