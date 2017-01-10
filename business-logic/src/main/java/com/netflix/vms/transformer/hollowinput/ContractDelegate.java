package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface ContractDelegate extends HollowObjectDelegate {

    public long getContractId(int ordinal);

    public Long getContractIdBoxed(int ordinal);

    public boolean getOriginal(int ordinal);

    public Boolean getOriginalBoxed(int ordinal);

    public int getCupTokenOrdinal(int ordinal);

    public boolean getDayOfBroadcast(int ordinal);

    public Boolean getDayOfBroadcastBoxed(int ordinal);

    public long getPrePromotionDays(int ordinal);

    public Long getPrePromotionDaysBoxed(int ordinal);

    public boolean getDayAfterBroadcast(int ordinal);

    public Boolean getDayAfterBroadcastBoxed(int ordinal);

    public int getDisallowedAssetBundlesOrdinal(int ordinal);

    public ContractTypeAPI getTypeAPI();

}