package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoDisplaySetSetsChildrenChildrenDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDisplaySetSetsChildrenChildrenDelegate {

    private final Long parentSequenceNumber;
    private final Long sequenceNumber;
    private final Long movieId;
    private final Long altId;
   private VideoDisplaySetSetsChildrenChildrenTypeAPI typeAPI;

    public VideoDisplaySetSetsChildrenChildrenDelegateCachedImpl(VideoDisplaySetSetsChildrenChildrenTypeAPI typeAPI, int ordinal) {
        this.parentSequenceNumber = typeAPI.getParentSequenceNumberBoxed(ordinal);
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.altId = typeAPI.getAltIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getParentSequenceNumber(int ordinal) {
        return parentSequenceNumber.longValue();
    }

    public Long getParentSequenceNumberBoxed(int ordinal) {
        return parentSequenceNumber;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    public long getAltId(int ordinal) {
        return altId.longValue();
    }

    public Long getAltIdBoxed(int ordinal) {
        return altId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoDisplaySetSetsChildrenChildrenTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoDisplaySetSetsChildrenChildrenTypeAPI) typeAPI;
    }

}