package com.netflix.vms.transformer.input.api.gen.award;

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

    public String getCountryCode(int ordinal) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(ordinal);
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        ordinal = typeAPI.getCountryCodeOrdinal(ordinal);
        return ordinal == -1 ? testValue == null : typeAPI.getAPI().getStringTypeAPI().isValueEqual(ordinal, testValue);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return typeAPI.getCountryCodeOrdinal(ordinal);
    }

    public long getSequenceNumber(int ordinal) {
        return typeAPI.getSequenceNumber(ordinal);
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return typeAPI.getSequenceNumberBoxed(ordinal);
    }

    public boolean getIsMovieAward(int ordinal) {
        return typeAPI.getIsMovieAward(ordinal);
    }

    public Boolean getIsMovieAwardBoxed(int ordinal) {
        return typeAPI.getIsMovieAwardBoxed(ordinal);
    }

    public long getFestivalId(int ordinal) {
        return typeAPI.getFestivalId(ordinal);
    }

    public Long getFestivalIdBoxed(int ordinal) {
        return typeAPI.getFestivalIdBoxed(ordinal);
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