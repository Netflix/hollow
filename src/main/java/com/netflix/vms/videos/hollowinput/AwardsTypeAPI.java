package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class AwardsTypeAPI extends HollowObjectTypeAPI {

    private final AwardsDelegateLookupImpl delegateLookupImpl;

    AwardsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "awardId",
            "description",
            "alternateName",
            "awardName"
        });
        this.delegateLookupImpl = new AwardsDelegateLookupImpl(this);
    }

    public long getAwardId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Awards", ordinal, "awardId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getAwardIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Awards", ordinal, "awardId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getDescriptionOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("Awards", ordinal, "description");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public AwardsDescriptionTypeAPI getDescriptionTypeAPI() {
        return getAPI().getAwardsDescriptionTypeAPI();
    }

    public int getAlternateNameOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Awards", ordinal, "alternateName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public AwardsAlternateNameTypeAPI getAlternateNameTypeAPI() {
        return getAPI().getAwardsAlternateNameTypeAPI();
    }

    public int getAwardNameOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Awards", ordinal, "awardName");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public AwardsAwardNameTypeAPI getAwardNameTypeAPI() {
        return getAPI().getAwardsAwardNameTypeAPI();
    }

    public AwardsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}