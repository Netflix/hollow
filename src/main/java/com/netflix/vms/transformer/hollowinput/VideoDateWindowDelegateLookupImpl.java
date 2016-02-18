package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDateWindowDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoDateWindowDelegate {

    private final VideoDateWindowTypeAPI typeAPI;

    public VideoDateWindowDelegateLookupImpl(VideoDateWindowTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public boolean getIsTheatricalRelease(int ordinal) {
        return typeAPI.getIsTheatricalRelease(ordinal);
    }

    public Boolean getIsTheatricalReleaseBoxed(int ordinal) {
        return typeAPI.getIsTheatricalReleaseBoxed(ordinal);
    }

    public long getStreetDate(int ordinal) {
        return typeAPI.getStreetDate(ordinal);
    }

    public Long getStreetDateBoxed(int ordinal) {
        return typeAPI.getStreetDateBoxed(ordinal);
    }

    public long getTheatricalReleaseDate(int ordinal) {
        return typeAPI.getTheatricalReleaseDate(ordinal);
    }

    public Long getTheatricalReleaseDateBoxed(int ordinal) {
        return typeAPI.getTheatricalReleaseDateBoxed(ordinal);
    }

    public int getTheatricalReleaseYear(int ordinal) {
        return typeAPI.getTheatricalReleaseYear(ordinal);
    }

    public Integer getTheatricalReleaseYearBoxed(int ordinal) {
        return typeAPI.getTheatricalReleaseYearBoxed(ordinal);
    }

    public VideoDateWindowTypeAPI getTypeAPI() {
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