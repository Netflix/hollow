package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class TrailerTrailersDelegateLookupImpl extends HollowObjectAbstractDelegate implements TrailerTrailersDelegate {

    private final TrailerTrailersTypeAPI typeAPI;

    public TrailerTrailersDelegateLookupImpl(TrailerTrailersTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getThemesOrdinal(int ordinal) {
        return typeAPI.getThemesOrdinal(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public int getIdentifierOrdinal(int ordinal) {
        return typeAPI.getIdentifierOrdinal(ordinal);
    }

    public int getPostPlayOrdinal(int ordinal) {
        return typeAPI.getPostPlayOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public int getSubTypeOrdinal(int ordinal) {
        return typeAPI.getSubTypeOrdinal(ordinal);
    }

    public int getAspectRatioOrdinal(int ordinal) {
        return typeAPI.getAspectRatioOrdinal(ordinal);
    }

    public TrailerTrailersTypeAPI getTypeAPI() {
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