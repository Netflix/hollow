package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;

public class VideoRightsContractDelegateLookupImpl extends HollowObjectAbstractDelegate implements VideoRightsContractDelegate {

    private final VideoRightsContractTypeAPI typeAPI;

    public VideoRightsContractDelegateLookupImpl(VideoRightsContractTypeAPI typeAPI) {
        this.typeAPI = typeAPI;
    }

    public long getContractId(int ordinal) {
        return typeAPI.getContractId(ordinal);
    }

    public Long getContractIdBoxed(int ordinal) {
        return typeAPI.getContractIdBoxed(ordinal);
    }

    public long getPackageId(int ordinal) {
        return typeAPI.getPackageId(ordinal);
    }

    public Long getPackageIdBoxed(int ordinal) {
        return typeAPI.getPackageIdBoxed(ordinal);
    }

    public int getCupTokenOrdinal(int ordinal) {
        return typeAPI.getCupTokenOrdinal(ordinal);
    }

    public long getPrePromotionDays(int ordinal) {
        return typeAPI.getPrePromotionDays(ordinal);
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        return typeAPI.getPrePromotionDaysBoxed(ordinal);
    }

    public boolean getDayAfterBroadcast(int ordinal) {
        return typeAPI.getDayAfterBroadcast(ordinal);
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        return typeAPI.getDayAfterBroadcastBoxed(ordinal);
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        return typeAPI.getDisallowedAssetBundlesOrdinal(ordinal);
    }

    public int getAssetsOrdinal(int ordinal) {
        return typeAPI.getAssetsOrdinal(ordinal);
    }

    public int getPackagesOrdinal(int ordinal) {
        return typeAPI.getPackagesOrdinal(ordinal);
    }

    public VideoRightsContractTypeAPI getTypeAPI() {
        return typeAPI;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

}