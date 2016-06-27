package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

@SuppressWarnings("all")
public class IndividualSupplementalDelegateLookupImpl extends HollowObjectAbstractDelegate implements IndividualSupplementalDelegate {

    private final IndividualSupplementalTypeAPI typeAPI;

    public IndividualSupplementalDelegateLookupImpl(IndividualSupplementalTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public int getIdentifierOrdinal(int ordinal) {
        return typeAPI.getIdentifierOrdinal(ordinal);
    }

    public long getMovieId(int ordinal) {
        return typeAPI.getMovieId(ordinal);
    }

    public Long getMovieIdBoxed(int ordinal) {
        return typeAPI.getMovieIdBoxed(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public int getPassthroughOrdinal(int ordinal) {
        return typeAPI.getPassthroughOrdinal(ordinal);
    }

    public IndividualSupplementalTypeAPI getTypeAPI() {
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