package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class MoviesOriginalTitleTranslatedTextsDelegateLookupImpl extends HollowObjectAbstractDelegate implements MoviesOriginalTitleTranslatedTextsDelegate {

    private final MoviesOriginalTitleTranslatedTextsTypeAPI typeAPI;

    public MoviesOriginalTitleTranslatedTextsDelegateLookupImpl(MoviesOriginalTitleTranslatedTextsTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getValueOrdinal(int ordinal) {
        return typeAPI.getValueOrdinal(ordinal);
    }

    public MoviesOriginalTitleTranslatedTextsTypeAPI getTypeAPI() {
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