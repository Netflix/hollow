package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CacheDeploymentIntentTypeAPI extends HollowObjectTypeAPI {

    private final CacheDeploymentIntentDelegateLookupImpl delegateLookupImpl;

    CacheDeploymentIntentTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "bitrateKBPS",
            "streamProfileId",
            "isoCountryCode"
        });
        this.delegateLookupImpl = new CacheDeploymentIntentDelegateLookupImpl(this);
    }

    public long getBitrateKBPS(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "bitrateKBPS");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getBitrateKBPSBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "bitrateKBPS");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getStreamProfileId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "streamProfileId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "streamProfileId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getIsoCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("CacheDeploymentIntent", ordinal, "isoCountryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getIsoCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public CacheDeploymentIntentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}