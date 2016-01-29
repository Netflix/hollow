package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollowFactory<T extends ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow(((ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsHollow(new ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsDelegateCachedImpl((ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}