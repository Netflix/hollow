package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

@SuppressWarnings("all")
public class PackageTypeAPI extends HollowObjectTypeAPI {

    private final PackageDelegateLookupImpl delegateLookupImpl;

    PackageTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "packageId",
            "movieId",
            "moments",
            "drmInfo",
            "downloadables",
            "defaultS3PathComponent"
        });
        this.delegateLookupImpl = new PackageDelegateLookupImpl(this);
    }

    public long getPackageId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("Package", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("Package", ordinal, "packageId");
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
            return missingDataHandler().handleLong("Package", ordinal, "movieId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getMovieIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("Package", ordinal, "movieId");
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
            return missingDataHandler().handleReferencedOrdinal("Package", ordinal, "moments");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public PackageMomentListTypeAPI getMomentsTypeAPI() {
        return getAPI().getPackageMomentListTypeAPI();
    }

    public int getDrmInfoOrdinal(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleReferencedOrdinal("Package", ordinal, "drmInfo");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[3]);
    }

    public PackageDrmInfoListTypeAPI getDrmInfoTypeAPI() {
        return getAPI().getPackageDrmInfoListTypeAPI();
    }

    public int getDownloadablesOrdinal(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleReferencedOrdinal("Package", ordinal, "downloadables");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[4]);
    }

    public PackageStreamSetTypeAPI getDownloadablesTypeAPI() {
        return getAPI().getPackageStreamSetTypeAPI();
    }

    public int getDefaultS3PathComponentOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("Package", ordinal, "defaultS3PathComponent");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public StringTypeAPI getDefaultS3PathComponentTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public PackageDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}