package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;
import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, FeedMovieCountryLanguagesDelegate {

    private final int movieIdOrdinal;
    private final int countryCodeOrdinal;
    private final int languageToEarliestWindowStartDateMapOrdinal;
    private FeedMovieCountryLanguagesTypeAPI typeAPI;

    public FeedMovieCountryLanguagesDelegateCachedImpl(FeedMovieCountryLanguagesTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.languageToEarliestWindowStartDateMapOrdinal = typeAPI.getLanguageToEarliestWindowStartDateMapOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public int getLanguageToEarliestWindowStartDateMapOrdinal(int ordinal) {
        return languageToEarliestWindowStartDateMapOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public FeedMovieCountryLanguagesTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (FeedMovieCountryLanguagesTypeAPI) typeAPI;
    }

}