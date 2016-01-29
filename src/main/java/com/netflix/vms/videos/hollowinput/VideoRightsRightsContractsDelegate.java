package com.netflix.vms.videos.hollowinput;

import com.netflix.hollow.objects.delegate.HollowObjectDelegate;

public interface VideoRightsRightsContractsDelegate extends HollowObjectDelegate {

    public int getDisallowedAssetBundlesOrdinal(int ordinal);

    public int getAssetsOrdinal(int ordinal);

    public int getCupTokenOrdinal(int ordinal);

    public long getContractId(int ordinal);

    public Long getContractIdBoxed(int ordinal);

    public long getPackageId(int ordinal);

    public Long getPackageIdBoxed(int ordinal);

    public long getPrePromotionDays(int ordinal);

    public Long getPrePromotionDaysBoxed(int ordinal);

    public boolean getDayAfterBroadcast(int ordinal);

    public Boolean getDayAfterBroadcastBoxed(int ordinal);

    public int getPackagesOrdinal(int ordinal);

    public VideoRightsRightsContractsTypeAPI getTypeAPI();

}