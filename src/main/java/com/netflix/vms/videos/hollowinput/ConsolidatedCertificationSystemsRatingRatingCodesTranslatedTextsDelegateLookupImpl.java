package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsDelegate {

    private final ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI typeAPI;

    public ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsDelegateLookupImpl(ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public ConsolidatedCertificationSystemsRatingRatingCodesTranslatedTextsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}