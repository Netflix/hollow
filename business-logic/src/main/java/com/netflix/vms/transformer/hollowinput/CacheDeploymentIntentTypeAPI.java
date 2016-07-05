package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class CacheDeploymentIntentTypeAPI extends HollowObjectTypeAPI {

    private final CacheDeploymentIntentDelegateLookupImpl delegateLookupImpl;

    CacheDeploymentIntentTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "streamProfileId",
            "isoCountryCode",
            "bitrateKBPS"
        });
        this.delegateLookupImpl = new CacheDeploymentIntentDelegateLookupImpl(this);
    }

    public long getStreamProfileId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "streamProfileId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getStreamProfileIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "streamProfileId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getIsoCountryCodeOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("CacheDeploymentIntent", ordinal, "isoCountryCode");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public StringTypeAPI getIsoCountryCodeTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getBitrateKBPS(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "bitrateKBPS");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getBitrateKBPSBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("CacheDeploymentIntent", ordinal, "bitrateKBPS");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public CacheDeploymentIntentDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}