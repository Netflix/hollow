package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class MovieRatingsRatingReasonTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements MovieRatingsRatingReasonTranslatedTextsDelegate {

    private final MovieRatingsRatingReasonTranslatedTextsTypeAPI typeAPI;

    public MovieRatingsRatingReasonTranslatedTextsDelegateLookupImpl(MovieRatingsRatingReasonTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public MovieRatingsRatingReasonTranslatedTextsTypeAPI getTypeAPI() {
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