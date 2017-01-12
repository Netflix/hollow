package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VMSAwardDelegateLookupImpl extends HollowObjectAbstractDelegate implements VMSAwardDelegate {

    private final VMSAwardTypeAPI typeAPI;

    public VMSAwardDelegateLookupImpl(VMSAwardTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getAwardId(int ordinal) {
        return typeAPI.getAwardId(ordinal);
    }

    public Long getAwardIdBoxed(int ordinal) {
        return typeAPI.getAwardIdBoxed(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public long getFestivalId(int ordinal) {
        return typeAPI.getFestivalId(ordinal);
    }

    public Long getFestivalIdBoxed(int ordinal) {
        return typeAPI.getFestivalIdBoxed(ordinal);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public boolean getIsMovieAward(int ordinal) {
        return typeAPI.getIsMovieAward(ordinal);
    }

    public Boolean getIsMovieAwardBoxed(int ordinal) {
        return typeAPI.getIsMovieAwardBoxed(ordinal);
    }

    public VMSAwardTypeAPI getTypeAPI() {
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