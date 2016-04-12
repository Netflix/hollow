package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class RightsDeployablePackagesTypeAPI extends HollowObjectTypeAPI {

    private final RightsDeployablePackagesDelegateLookupImpl delegateLookupImpl;

    RightsDeployablePackagesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "movieId",
            "countryCodes"
        });
        this.delegateLookupImpl = new RightsDeployablePackagesDelegateLookupImpl(this);
    }

    public long getPackageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("RightsDeployablePackages", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("RightsDeployablePackages", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMovieId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("RightsDeployablePackages", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("RightsDeployablePackages", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("RightsDeployablePackages", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public ISOCountrySetTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getISOCountrySetTypeAPI();
    }

    public RightsDeployablePackagesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}