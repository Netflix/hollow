package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesDelegateLookupImpl extends HollowObjectAbstractDelegate implements FeedMovieCountryLanguagesDelegate {

    private final FeedMovieCountryLanguagesTypeAPI typeAPI;

    public FeedMovieCountryLanguagesDelegateLookupImpl(FeedMovieCountryLanguagesTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return typeAPI.getMovieIdOrdinal(ordinal);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public int getLanguageCodeOrdinal(int ordinal) {
        return typeAPI.getLanguageCodeOrdinal(ordinal);
    }

    public int getEarliestWindowStartDateOrdinal(int ordinal) {
        return typeAPI.getEarliestWindowStartDateOrdinal(ordinal);
    }

    public FeedMovieCountryLanguagesTypeAPI getTypeAPI() {
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