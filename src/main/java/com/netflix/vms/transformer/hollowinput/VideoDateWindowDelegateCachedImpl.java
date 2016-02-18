package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoDateWindowDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDateWindowDelegate {

    private final int countryCodeOrdinal;
    private final Boolean isTheatricalRelease;
    private final Long streetDate;
    private final Long theatricalReleaseDate;
    private final Integer theatricalReleaseYear;
   private VideoDateWindowTypeAPI typeAPI;

    public VideoDateWindowDelegateCachedImpl(VideoDateWindowTypeAPI typeAPI, int ordinal) {
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.isTheatricalRelease = typeAPI.getIsTheatricalReleaseBoxed(ordinal);
        this.streetDate = typeAPI.getStreetDateBoxed(ordinal);
        this.theatricalReleaseDate = typeAPI.getTheatricalReleaseDateBoxed(ordinal);
        this.theatricalReleaseYear = typeAPI.getTheatricalReleaseYearBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public boolean getIsTheatricalRelease(int ordinal) {
        return isTheatricalRelease.booleanValue();
    }

    public Boolean getIsTheatricalReleaseBoxed(int ordinal) {
        return isTheatricalRelease;
    }

    public long getStreetDate(int ordinal) {
        return streetDate.longValue();
    }

    public Long getStreetDateBoxed(int ordinal) {
        return streetDate;
    }

    public long getTheatricalReleaseDate(int ordinal) {
        return theatricalReleaseDate.longValue();
    }

    public Long getTheatricalReleaseDateBoxed(int ordinal) {
        return theatricalReleaseDate;
    }

    public int getTheatricalReleaseYear(int ordinal) {
        return theatricalReleaseYear.intValue();
    }

    public Integer getTheatricalReleaseYearBoxed(int ordinal) {
        return theatricalReleaseYear;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoDateWindowTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoDateWindowTypeAPI) typeAPI;
    }

}