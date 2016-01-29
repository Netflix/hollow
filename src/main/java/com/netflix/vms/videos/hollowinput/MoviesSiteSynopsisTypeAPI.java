package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class MoviesSiteSynopsisTypeAPI extends HollowObjectTypeAPI {

    private final MoviesSiteSynopsisDelegateLookupImpl delegateLookupImpl;

    MoviesSiteSynopsisTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "translatedTexts"
        });
        this.delegateLookupImpl = new MoviesSiteSynopsisDelegateLookupImpl(this);
    }

    public int getTranslatedTextsOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("MoviesSiteSynopsis", ordinal, "translatedTexts");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public MoviesSiteSynopsisMapOfTranslatedTextsTypeAPI getTranslatedTextsTypeAPI() {
        return getAPI().getMoviesSiteSynopsisMapOfTranslatedTextsTypeAPI();
    }

    public MoviesSiteSynopsisDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}