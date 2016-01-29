package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoDisplaySetSetsChildrenDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoDisplaySetSetsChildrenDelegate {

    private final Long sequenceNumber;
    private final int childrenOrdinal;
    private final Long movieId;
   private VideoDisplaySetSetsChildrenTypeAPI typeAPI;

    public VideoDisplaySetSetsChildrenDelegateCachedImpl(VideoDisplaySetSetsChildrenTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.childrenOrdinal = typeAPI.getChildrenOrdinal(ordinal);
        this.movieId = typeAPI.getMovieIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public int getChildrenOrdinal(int ordinal) {
        return childrenOrdinal;
    }

    public long getMovieId(int ordinal) {
        return movieId.longValue();
    }

    public Long getMovieIdBoxed(int ordinal) {
        return movieId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoDisplaySetSetsChildrenTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoDisplaySetSetsChildrenTypeAPI) typeAPI;
    }

}