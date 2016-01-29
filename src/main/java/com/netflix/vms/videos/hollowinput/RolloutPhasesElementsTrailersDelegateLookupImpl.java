package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class RolloutPhasesElementsTrailersDelegateLookupImpl extends HollowObjectAbstractDelegate implements RolloutPhasesElementsTrailersDelegate {

    private final RolloutPhasesElementsTrailersTypeAPI typeAPI;

    public RolloutPhasesElementsTrailersDelegateLookupImpl(RolloutPhasesElementsTrailersTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getTrailerMovieId(int ordinal) {
        return typeAPI.getTrailerMovieId(ordinal);
    }

    public Long getTrailerMovieIdBoxed(int ordinal) {
        return typeAPI.getTrailerMovieIdBoxed(ordinal);
    }

    public int getSupplementalInfoOrdinal(int ordinal) {
        return typeAPI.getSupplementalInfoOrdinal(ordinal);
    }

    public RolloutPhasesElementsTrailersTypeAPI getTypeAPI() {
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