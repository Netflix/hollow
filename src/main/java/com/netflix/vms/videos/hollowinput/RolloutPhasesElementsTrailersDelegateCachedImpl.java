package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class RolloutPhasesElementsTrailersDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, RolloutPhasesElementsTrailersDelegate {

    private final Long sequenceNumber;
    private final Long trailerMovieId;
    private final int supplementalInfoOrdinal;
   private RolloutPhasesElementsTrailersTypeAPI typeAPI;

    public RolloutPhasesElementsTrailersDelegateCachedImpl(RolloutPhasesElementsTrailersTypeAPI typeAPI, int ordinal) {
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.trailerMovieId = typeAPI.getTrailerMovieIdBoxed(ordinal);
        this.supplementalInfoOrdinal = typeAPI.getSupplementalInfoOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public long getTrailerMovieId(int ordinal) {
        return trailerMovieId.longValue();
    }

    public Long getTrailerMovieIdBoxed(int ordinal) {
        return trailerMovieId;
    }

    public int getSupplementalInfoOrdinal(int ordinal) {
        return supplementalInfoOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public RolloutPhasesElementsTrailersTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (RolloutPhasesElementsTrailersTypeAPI) typeAPI;
    }

}