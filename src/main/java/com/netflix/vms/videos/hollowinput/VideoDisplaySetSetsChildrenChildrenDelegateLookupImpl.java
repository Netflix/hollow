package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoDisplaySetSetsChildrenChildrenDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoDisplaySetSetsChildrenChildrenDelegate {

    private final VideoDisplaySetSetsChildrenChildrenTypeAPI typeAPI;

    public VideoDisplaySetSetsChildrenChildrenDelegateLookupImpl(VideoDisplaySetSetsChildrenChildrenTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getParentSequenceNumber(int ordinal) {
        return typeAPI.getParentSequenceNumber(ordinal);
    }

    public Long getParentSequenceNumberBoxed(int ordinal) {
        return typeAPI.getParentSequenceNumberBoxed(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public long getAltId(int ordinal) {
        return typeAPI.getAltId(ordinal);
    }

    public Long getAltIdBoxed(int ordinal) {
        return typeAPI.getAltIdBoxed(ordinal);
    }

    public VideoDisplaySetSetsChildrenChildrenTypeAPI getTypeAPI() {
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