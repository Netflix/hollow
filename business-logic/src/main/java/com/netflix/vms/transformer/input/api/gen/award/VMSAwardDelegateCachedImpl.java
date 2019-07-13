package com.netflix.vms.transformer.input.api.gen.award;

import com.netflix.hollow.api.custom.HollowTypeAPI;
import com.netflix.hollow.api.objects.delegate.HollowCachedDelegate;
import com.netflix.hollow.api.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.core.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.core.schema.HollowObjectSchema;

@SuppressWarnings("all")
public class VMSAwardDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VMSAwardDelegate {

    private final Long awardId;
    private final String countryCode;
    private final int countryCodeOrdinal;
    private final Long sequenceNumber;
    private final Boolean isMovieAward;
    private final Long festivalId;
    private VMSAwardTypeAPI typeAPI;

    public VMSAwardDelegateCachedImpl(VMSAwardTypeAPI typeAPI, int ordinal) {
        this.awardId = typeAPI.getAwardIdBoxed(ordinal);
        this.countryCodeOrdinal = typeAPI.getCountryCodeOrdinal(ordinal);
        int countryCodeTempOrdinal = countryCodeOrdinal;
        this.countryCode = countryCodeTempOrdinal == -1 ? null : typeAPI.getAPI().getStringTypeAPI().getValue(countryCodeTempOrdinal);
        this.sequenceNumber = typeAPI.getSequenceNumberBoxed(ordinal);
        this.isMovieAward = typeAPI.getIsMovieAwardBoxed(ordinal);
        this.festivalId = typeAPI.getFestivalIdBoxed(ordinal);
        this.typeAPI = typeAPI;
    }

    public long getAwardId(int ordinal) {
        if(awardId == null)
            return Long.MIN_VALUE;
        return awardId.longValue();
    }

    public Long getAwardIdBoxed(int ordinal) {
        return awardId;
    }

    public String getCountryCode(int ordinal) {
        return countryCode;
    }

    public boolean isCountryCodeEqual(int ordinal, String testValue) {
        if(testValue == null)
            return countryCode == null;
        return testValue.equals(countryCode);
    }

    public int getCountryCodeOrdinal(int ordinal) {
        return countryCodeOrdinal;
    }

    public long getSequenceNumber(int ordinal) {
        if(sequenceNumber == null)
            return Long.MIN_VALUE;
        return sequenceNumber.longValue();
    }

    public Long getSequenceNumberBoxed(int ordinal) {
        return sequenceNumber;
    }

    public boolean getIsMovieAward(int ordinal) {
        if(isMovieAward == null)
            return false;
        return isMovieAward.booleanValue();
    }

    public Boolean getIsMovieAwardBoxed(int ordinal) {
        return isMovieAward;
    }

    public long getFestivalId(int ordinal) {
        if(festivalId == null)
            return Long.MIN_VALUE;
        return festivalId.longValue();
    }

    public Long getFestivalIdBoxed(int ordinal) {
        return festivalId;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VMSAwardTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VMSAwardTypeAPI) typeAPI;
    }

}