package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowMapCachedDelegate;

public class ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollowFactory<T extends ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow(((ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsHollow(new HollowMapCachedDelegate((ConsolidatedCertificationSystemsRatingRatingCodesMapOfTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}