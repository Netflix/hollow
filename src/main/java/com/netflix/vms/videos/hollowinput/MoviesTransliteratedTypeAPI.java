package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class MoviesTransliteratedTypeAPI extends HollowObjectTypeAPI {

    private final MoviesTransliteratedDelegateLookupImpl delegateLookupImpl;

    MoviesTransliteratedTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new MoviesTransliteratedDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MoviesTransliterated", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MoviesTransliteratedMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMoviesTransliteratedMapOfTranslatedTextsTypeAPI();
    }

    public MoviesTransliteratedDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}