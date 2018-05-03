package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.provider.HollowFactory;
import com.netflix.hollow.core.read.dataaccess.HollowTypeDataAccess;
import com.netflix.hollow.api.custom.HollowTypeAPI;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesHollowFactory<T extends FeedMovieCountryLanguagesHollow> extends HollowFactory<T> {

    @Override
    public T newHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new FeedMovieCountryLanguagesHollow(((FeedMovieCountryLanguagesTypeAPI)typeAPI).getDelegateLookupImpl(), ordinal);
    }

    @Override
    public T newCachedHollowObject(HollowTypeDataAccess dataAccess, HollowTypeAPI typeAPI, int ordinal) {
        return (T)new FeedMovieCountryLanguagesHollow(new FeedMovieCountryLanguagesDelegateCachedImpl((FeedMovieCountryLanguagesTypeAPI)typeAPI, ordinal), ordinal);
    }

}