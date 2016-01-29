package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoDateWindowDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDateWindowDelegate {

    private final Boolean isTheatricalRelease;
    private final int countryCodeOrdinal;
    private final Long streetDate;
    private final Long theatricalReleaseYear;
    private final Long theatricalReleaseDate;
   private VideoDateWindowTypeAPI typeAPI;

    public VideoDateWindowDelegateCachedImpl(VideoDateWindowTypeAPI typeAPI, int ordinal) {
        this.isTheatricalRelease = typeAPI.getIsTheatricalReleaseBoxed(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        this.streetDate = typeAPI.getStreetDateBoxed(ordinal);
        this.theatricalReleaseYear = typeAPI.getTheatricalReleaseYearBoxed(ordinal);
        this.theatricalReleaseDate = typeAPI.getTheatricalReleaseDateBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public boolean getIsTheatricalRelease(int ordinal) {
        return isTheatricalRelease.booleanValue();
    }

    public Boolean getIsTheatricalReleaseBoxed(int ordinal) {
        return isTheatricalRelease;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public long getStreetDate(int ordinal) {
        return streetDate.longValue();
    }

    public Long getStreetDateBoxed(int ordinal) {
        return streetDate;
    }

    public long getTheatricalReleaseYear(int ordinal) {
        return theatricalReleaseYear.longValue();
    }

    public Long getTheatricalReleaseYearBoxed(int ordinal) {
        return theatricalReleaseYear;
    }

    public long getTheatricalReleaseDate(int ordinal) {
        return theatricalReleaseDate.longValue();
    }

    public Long getTheatricalReleaseDateBoxed(int ordinal) {
        return theatricalReleaseDate;
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