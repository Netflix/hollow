package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsRightsContractsTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsRightsContractsDelegateLookupImpl delegateLookupImpl;

    VideoRightsRightsContractsTypeAPI(VMSHollowVideoInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "disallowedAssetBundles",
            "assets",
            "cupToken",
            "contractId",
            "packageId",
            "prePromotionDays",
            "dayAfterBroadcast",
            "packages"
        });
        this.delegateLookupImpl = new VideoRightsRightsContractsDelegateLookupImpl(this);
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContracts", ordinal, "disallowedAssetBundles");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[0]);
    }

    public VideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI getDisallowedAssetBundlesTypeAPI() {
        return getAPI().getVideoRightsRightsContractsArrayOfDisallowedAssetBundlesTypeAPI();
    }

    public int getAssetsOrdinal(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContracts", ordinal, "assets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[1]);
    }

    public VideoRightsRightsContractsArrayOfAssetsTypeAPI getAssetsTypeAPI() {
        return getAPI().getVideoRightsRightsContractsArrayOfAssetsTypeAPI();
    }

    public int getCupTokenOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContracts", ordinal, "cupToken");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCupTokenTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getContractId(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsContracts", ordinal, "contractId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getContractIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsContracts", ordinal, "contractId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPackageId(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsContracts", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[4] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsContracts", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[4]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[4]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPrePromotionDays(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleLong("VideoRightsRightsContracts", ordinal, "prePromotionDays");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        long l;
        if(fieldIndex[5] == -1) {
            l = missingDataHandler().handleLong("VideoRightsRightsContracts", ordinal, "prePromotionDays");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[5]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[5]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getDayAfterBroadcast(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoRightsRightsContracts", ordinal, "dayAfterBroadcast") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]) == Boolean.TRUE;
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleBoolean("VideoRightsRightsContracts", ordinal, "dayAfterBroadcast");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[6]);
    }



    public int getPackagesOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsRightsContracts", ordinal, "packages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public VideoRightsRightsContractsArrayOfPackagesTypeAPI getPackagesTypeAPI() {
        return getAPI().getVideoRightsRightsContractsArrayOfPackagesTypeAPI();
    }

    public VideoRightsRightsContractsDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowVideoInputAPI getAPI() {
        return (VMSHollowVideoInputAPI) api;
    }

}