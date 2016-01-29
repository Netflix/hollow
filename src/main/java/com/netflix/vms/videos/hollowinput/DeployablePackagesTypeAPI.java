package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class DeployablePackagesTypeAPI extends HollowObjectTypeAPI {

    private final DeployablePackagesDelegateLookupImpl delegateLookupImpl;

    DeployablePackagesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "countryCodes",
            "packageId",
            "movieId"
        });
        this.delegateLookupImpl = new DeployablePackagesDelegateLookupImpl(this);
    }

    public int getCountryCodesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("DeployablePackages", ordinal, "countryCodes");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public DeployablePackagesArrayOfCountryCodesTypeAPI getCountryCodesTypeAPI() {
        return getAPI().getDeployablePackagesArrayOfCountryCodesTypeAPI();
    }

    public long getPackageId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("DeployablePackages", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("DeployablePackages", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getMovieId(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleLong("DeployablePackages", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[2] == -1) {
            l = missingDataHandler().handleLong("DeployablePackages", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[2]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[2]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public DeployablePackagesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}