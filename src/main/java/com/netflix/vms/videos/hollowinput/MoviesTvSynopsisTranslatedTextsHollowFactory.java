package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.provider.HollowFactory;
import com.netflix.hollow.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.read.customapi.HollowTypeAPI;

public class MoviesTvSynopsisTranslatedTextsHollowFactory<T extends MoviesTvSynopsisTranslatedTextsHollow> extends HollowFactory<T> {

    @Override
    @SuppressWarnings("unchecked")
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MoviesTvSynopsisTranslatedTextsHollow(((MoviesTvSynopsisTranslatedTextsTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new MoviesTvSynopsisTranslatedTextsHollow(new MoviesTvSynopsisTranslatedTextsDelegateCachedImpl((MoviesTvSynopsisTranslatedTextsTypeAPI)typeAPI, ordinal), ordinal);
    }

}