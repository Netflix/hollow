package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectAbstractDelegate;
import com.netflix.hollow.read.dataaccess.HollowObjectTypeDataAccess;
import com.netflix.hollow.HollowObjectSchema;
import com.netflix.hollow.read.customapi.HollowTypeAPI;
import com.netflix.hollow.objects.delegate.HollowCachedDelegate;

public class VideoRightsRightsContractsDelegateCachedImpl extends HollowObjectAbstractDelegate implements HollowCachedDelegate, VideoRightsRightsContractsDelegate {

    private final int disallowedAssetBundlesOrdinal;
    private final int assetsOrdinal;
    private final int cupTokenOrdinal;
    private final Long contractId;
    private final Long packageId;
    private final Long prePromotionDays;
    private final Boolean dayAfterBroadcast;
    private final int packagesOrdinal;
   private VideoRightsRightsContractsTypeAPI typeAPI;

    public VideoRightsRightsContractsDelegateCachedImpl(VideoRightsRightsContractsTypeAPI typeAPI, int ordinal) {
        this.disallowedAssetBundlesOrdinal = typeAPI.getDisallowedAssetBundlesOrdinal(ordinal);
        this.assetsOrdinal = typeAPI.getAssetsOrdinal(ordinal);
        this.cupTokenOrdinal = typeAPI.getCupTokenOrdinal(ordinal);
        this.contractId = typeAPI.getContractIdBoxed(ordinal);
        this.packageId = typeAPI.getPackageIdBoxed(ordinal);
        this.prePromotionDays = typeAPI.getPrePromotionDaysBoxed(ordinal);
        this.dayAfterBroadcast = typeAPI.getDayAfterBroadcastBoxed(ordinal);
        this.packagesOrdinal = typeAPI.getPackagesOrdinal(ordinal);
        this.typeAPI = typeAPI;
    }

    public int getDisallowedAssetBundlesOrdinal(int ordinal) {
        return disallowedAssetBundlesOrdinal;
    }

    public int getAssetsOrdinal(int ordinal) {
        return assetsOrdinal;
    }

    public int getCupTokenOrdinal(int ordinal) {
        return cupTokenOrdinal;
    }

    public long getContractId(int ordinal) {
        return contractId.longValue();
    }

    public Long getContractIdBoxed(int ordinal) {
        return contractId;
    }

    public long getPackageId(int ordinal) {
        return packageId.longValue();
    }

    public Long getPackageIdBoxed(int ordinal) {
        return packageId;
    }

    public long getPrePromotionDays(int ordinal) {
        return prePromotionDays.longValue();
    }

    public Long getPrePromotionDaysBoxed(int ordinal) {
        return prePromotionDays;
    }

    public boolean getDayAfterBroadcast(int ordinal) {
        return dayAfterBroadcast.booleanValue();
    }

    public Boolean getDayAfterBroadcastBoxed(int ordinal) {
        return dayAfterBroadcast;
    }

    public int getPackagesOrdinal(int ordinal) {
        return packagesOrdinal;
    }

    @Override
    public HollowObjectSchema getSchema() {
        return typeAPI.getTypeDataAccess().getSchema();
    }

    @Override
    public HollowObjectTypeDataAccess getTypeDataAccess() {
        return typeAPI.getTypeDataAccess();
    }

    public VideoRightsRightsContractsTypeAPI getTypeAPI() {
        return typeAPI;
    }

    public void updateTypeAPI(HollowTypeAPI typeAPI) {
        this.typeAPI = (VideoRightsRightsContractsTypeAPI) typeAPI;
    }

}