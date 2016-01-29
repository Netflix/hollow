package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoPersonTypeAPI extends HollowObjectTypeAPI {

    private final VideoPersonDelegateLookupImpl delegateLookupImpl;

    VideoPersonTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "cast",
            "alias",
            "personId"
        });
        this.delegateLookupImpl = new VideoPersonDelegateLookupImpl(this);
    }

    public int getCastOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoPerson", ordinal, "cast");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoPersonArrayOfCastTypeAPI getCastTypeAPI() {
        return getAPI().getVideoPersonArrayOfCastTypeAPI();
    }

    public int getAliasOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoPerson", ordinal, "alias");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoPersonArrayOfAliasTypeAPI getAliasTypeAPI() {
        return getAPI().getVideoPersonArrayOfAliasTypeAPI();
    }

    public long getPersonId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("VideoPerson", ordinal, "personId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getPersonIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("VideoPerson", ordinal, "personId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public VideoPersonDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}