package com.netflix.vms.transformer.hollowinput;

import com.netflix.hollow.api.objects.delegate.HollowObjectDelegate;


@SuppressWarnings("all")
public interface RightsWindowContractDelegate extends HollowObjectDelegate {

    public long getContractId(int ordinal);

    public Long getContractIdBoxed(int ordinal);

    public boolean getDownload(int ordinal);

    public Boolean getDownloadBoxed(int ordinal);

    public RightsWindowContractTypeAPI getTypeAPI();

}