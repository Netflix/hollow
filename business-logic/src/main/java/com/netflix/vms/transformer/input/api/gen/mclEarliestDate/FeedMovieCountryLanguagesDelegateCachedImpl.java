package com.netflix.vms.transformer.input.api.gen.mclEarliestDate;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class FeedMovieCountryLanguagesDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, FeedMovieCountryLanguagesDelegate {

    private final Long movieId;
    private final int movieIdOrdinal;
    private final String countryCode;
    private final int countryCodeOrdinal;
    private final int languageToEarliestWindowStartDateMapOrdinal;
    private FeedMovieCountryLanguagesTypeAPI typeAPI;

    public FeedMovieCountryLanguagesDelegateCachedImpl(FeedMovieCountryLanguagesTypeAPI typeAPI, int ordinal) {
        this.movieIdOrdinal = typeAPI.getMovieIdOrdinal(ordinal);
        int movieIdTempOrdinal = movieIdOrdinal;
        this.movieId = movieIdTempOrdinal == -1 ? null : typeAPI.getAPI().getLongTypeAPI().getValue(movieIdTempOrdinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        int countryCodeTempOrdinal = countryCodeOrdinal;
        this.countryCode = countryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryCodeTempOrdinal);
        this.languageToEarliestWindowStartDateMapOrdinal = typeAPI.getLanguageToEarliestWindowStartDateMapOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getMovieId(int ordinal) {
        if(movieId == null)
            return Long.MIN_VALUE;
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public int getMovieIdOrdinal(int ordinal) {
        return movieIdOrdinal;
    }

    public String getCountryCode(int ordinal) {
        return countryCode;
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryCode == null;
        return testValue.equals(countryCode);
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