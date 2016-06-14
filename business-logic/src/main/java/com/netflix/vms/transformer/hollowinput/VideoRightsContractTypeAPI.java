package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.read.customapi.HollowObjectTypeAPI;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;

public class VideoRightsContractTypeAPI extends HollowObjectTypeAPI {

    private final VideoRightsContractDelegateLookupImpl delegateLookupImpl;

    VideoRightsContractTypeAPI(VMSHollowInputAPI api, HollowObjectTypeDataAccess typeDataAccess) {
        super(api, typeDataAccess, new String[] {
            "contractId",
            "packageId",
            "cupToken",
            "prePromotionDays",
            "dayAfterBroadcast",
            "disallowedAssetBundles",
            "assets",
            "packages"
        });
        this.delegateLookupImpl = new VideoRightsContractDelegateLookupImpl(this);
    }

    public long getContractId(int ordinal) {
        if(fieldIndex[0] == -1)
            return missingDataHandler().handleLong("VideoRightsContract", ordinal, "contractId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
    }

    public Long getContractIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[0] == -1) {
            l = missingDataHandler().handleLong("VideoRightsContract", ordinal, "contractId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[0]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[0]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public long getPackageId(int ordinal) {
        if(fieldIndex[1] == -1)
            return missingDataHandler().handleLong("VideoRightsContract", ordinal, "packageId");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
    }

    public Long getPackageIdBoxed(int ordinal) {
        long l;
        if(fieldIndex[1] == -1) {
            l = missingDataHandler().handleLong("VideoRightsContract", ordinal, "packageId");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[1]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[1]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public int getCupTokenOrdinal(int ordinal) {
        if(fieldIndex[2] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsContract", ordinal, "cupToken");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[2]);
    }

    public StringTypeAPI getCupTokenTypeAPI() {
        return getAPI().getStringTypeAPI();
    }

    public long getPrePromotionDays(int ordinal) {
        if(fieldIndex[3] == -1)
            return missingDataHandler().handleLong("VideoRightsContract", ordinal, "prePromotionDays");
        return getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        long l;
        if(fieldIndex[3] == -1) {
            l = missingDataHandler().handleLong("VideoRightsContract", ordinal, "prePromotionDays");
        } else {
            boxedFieldAccessSampler.recordFieldAccess(fieldIndex[3]);
            l = getTypeDataAccess().readLong(ordinal, fieldIndex[3]);
        }
        if(l == Long.MIN_VALUE)
            return null;
        return Long.valueOf(l);
    }



    public boolean getDayAfterBroadcast(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoRightsContract", ordinal, "dayAfterBroadcast") == Boolean.TRUE;
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]) == Boolean.TRUE;
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        if(fieldIndex[4] == -1)
            return missingDataHandler().handleBoolean("VideoRightsContract", ordinal, "dayAfterBroadcast");
        return getTypeDataAccess().readBoolean(ordinal, fieldIndex[4]);
    }



    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        if(fieldIndex[5] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsContract", ordinal, "disallowedAssetBundles");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[5]);
    }

    public DisallowedAssetBundlesListTypeAPI getDisallowedAssetBundlesTypeAPI() {
        return getAPI().getDisallowedAssetBundlesListTypeAPI();
    }

    public int getAssetsOrdinal(int ordinal) {
        if(fieldIndex[6] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsContract", ordinal, "assets");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[6]);
    }

    public VideoRightsContractAssetsSetTypeAPI getAssetsTypeAPI() {
        return getAPI().getVideoRightsContractAssetsSetTypeAPI();
    }

    public int getPackagesOrdinal(int ordinal) {
        if(fieldIndex[7] == -1)
            return missingDataHandler().handleReferencedOrdinal("VideoRightsContract", ordinal, "packages");
        return getTypeDataAccess().readOrdinal(ordinal, fieldIndex[7]);
    }

    public VideoRightsContractPackagesListTypeAPI getPackagesTypeAPI() {
        return getAPI().getVideoRightsContractPackagesListTypeAPI();
    }

    public VideoRightsContractDelegateLookupImpl getDelegateLookupImpl() {
        return delegateLookupImpl;
    }

    @Override
    public VMSHollowInputAPI getAPI() {
        return (VMSHollowInputAPI) api;
    }

}