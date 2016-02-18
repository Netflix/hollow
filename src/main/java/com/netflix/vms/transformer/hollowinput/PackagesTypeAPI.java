package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class PackagesTypeAPI extends HollowObjectTypeAPI {

    private final PackagesDelegateLookupImpl delegateLookupImpl;

    PackagesTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "movieId",
            "moments",
            "drmInfo",
            "downloadables",
            "defaultS3PathComponent"
        });
        this.delegateLookupImpl = new PackagesDelegateLookupImpl(this);
    }

    public long getPackageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Packages", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Packages", ordinal, "packageId");
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
            return missingDataHandler().handleLong("Packages", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Packages", ordinal, "movieId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getMomentsOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("Packages", ordinal, "moments");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public PackageMomentListTypeAPI getMomentsTypeAPI() {
        return getAPI().getPackageMomentListTypeAPI();
    }

    public int getDrmInfoOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Packages", ordinal, "drmInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public PackageDrmInfoListTypeAPI getDrmInfoTypeAPI() {
        return getAPI().getPackageDrmInfoListTypeAPI();
    }

    public int getDownloadablesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Packages", ordinal, "downloadables");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public PackageStreamSetTypeAPI getDownloadablesTypeAPI() {
        return getAPI().getPackageStreamSetTypeAPI();
    }

    public int getDefaultS3PathComponentOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Packages", ordinal, "defaultS3PathComponent");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getDefaultS3PathComponentTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PackagesDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}